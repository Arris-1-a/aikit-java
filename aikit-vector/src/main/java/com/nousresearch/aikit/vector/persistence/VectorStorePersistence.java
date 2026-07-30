package com.nousresearch.aikit.vector.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nousresearch.aikit.core.VectorStore;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Handles persistence for vector stores, supporting both JSON and binary formats.
 *
 * <p>JSON format: human-readable, compressed with GZIP. Suitable for smaller
 * datasets and debugging.</p>
 *
 * <p>Binary format: compact, fast. Stores raw float arrays directly.
 * Recommended for production workloads with large vector counts.</p>
 */
public final class VectorStorePersistence {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    // Magic bytes for binary format: "AIKV" ⇒ 0x41494B56
    private static final int MAGIC = 0x41494B56;
    private static final int VERSION = 1;

    private VectorStorePersistence() {}

    /**
     * Saves a vector store to a file. Format is determined by file extension:
     * {@code .json} or {@code .json.gz} → JSON; otherwise → binary.
     *
     * @param store the vector store to save
     * @param path the file path
     */
    @SuppressWarnings("unchecked")
    public static <T> void save(VectorStore<T> store, String path) {
        Path filePath = Paths.get(path);
        String fileName = filePath.getFileName().toString().toLowerCase();

        if (fileName.endsWith(".json") || fileName.endsWith(".json.gz")) {
            saveAsJson(store, path);
        } else {
            saveAsBinary(store, path);
        }
    }

    /**
     * Loads a vector store from a file. Auto-detects format.
     *
     * @param store the vector store to populate
     * @param path the file path
     */
    @SuppressWarnings("unchecked")
    public static <T> void load(VectorStore<T> store, String path) {
        Path filePath = Paths.get(path);
        String fileName = filePath.getFileName().toString().toLowerCase();

        if (fileName.endsWith(".json") || fileName.endsWith(".json.gz")) {
            loadFromJson(store, path);
        } else {
            loadFromBinary(store, path);
        }
    }

    // ---- JSON persistence ----

    @SuppressWarnings("unchecked")
    private static <T> void saveAsJson(VectorStore<T> store, String path) {
        try {
            List<Map<String, Object>> entries = new ArrayList<>();
            // Iterate using the store's entries() method
            for (VectorStore.VectorEntry<T> entry : store.entries()) {
                Map<String, Object> entryMap = new LinkedHashMap<>();
                entryMap.put("id", entry.getId());
                // Convert float[] to List<Double> for JSON serialization
                List<Double> vecList = new ArrayList<>(entry.getVector().length);
                for (float v : entry.getVector()) {
                    vecList.add((double) v);
                }
                entryMap.put("vector", vecList);
                if (entry.getMetadata() != null) {
                    entryMap.put("metadata", entry.getMetadata());
                }
                entries.add(entryMap);
            }

            Map<String, Object> wrapper = new LinkedHashMap<>();
            wrapper.put("version", 1);
            wrapper.put("format", "aikit-vector-json");
            wrapper.put("size", entries.size());
            wrapper.put("entries", entries);

            String json = OBJECT_MAPPER.writeValueAsString(wrapper);

            Path filePath = Paths.get(path);
            if (path.endsWith(".gz")) {
                try (GZIPOutputStream gzos = new GZIPOutputStream(
                        new FileOutputStream(filePath.toFile()))) {
                    gzos.write(json.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                Files.writeString(filePath, json);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save vector store as JSON: " + path, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void loadFromJson(VectorStore<T> store, String path) {
        try {
            String json;
            if (path.endsWith(".gz")) {
                try (GZIPInputStream gzis = new GZIPInputStream(
                        new FileInputStream(path))) {
                    json = new String(gzis.readAllBytes(), StandardCharsets.UTF_8);
                }
            } else {
                json = Files.readString(Paths.get(path));
            }

            Map<String, Object> wrapper = OBJECT_MAPPER.readValue(
                    json, new TypeReference<Map<String, Object>>() {});

            List<Map<String, Object>> entries =
                    (List<Map<String, Object>>) wrapper.get("entries");
            if (entries != null) {
                for (Map<String, Object> e : entries) {
                    String id = (String) e.get("id");
                    List<Number> vecRaw = (List<Number>) e.get("vector");
                    float[] vector = new float[vecRaw.size()];
                    for (int i = 0; i < vecRaw.size(); i++) {
                        vector[i] = vecRaw.get(i).floatValue();
                    }
                    T metadata = (T) e.get("metadata");
                    store.add(id, vector, metadata);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load vector store from JSON: " + path, e);
        }
    }

    // ---- Binary persistence ----

    private static <T> void saveAsBinary(VectorStore<T> store, String path) {
        try (FileOutputStream fos = new FileOutputStream(path);
             DataOutputStream dos = new DataOutputStream(fos)) {

            // Write header: magic + version
            dos.writeInt(MAGIC);
            dos.writeInt(VERSION);

            // Collect entries before writing to get accurate count
            List<VectorStore.VectorEntry<T>> entryList = store.entries();
            dos.writeInt(entryList.size()); // count

            for (VectorStore.VectorEntry<T> entry : entryList) {
                // Write ID
                byte[] idBytes = entry.getId().getBytes(StandardCharsets.UTF_8);
                dos.writeInt(idBytes.length);
                dos.write(idBytes);

                // Write vector
                float[] vector = entry.getVector();
                dos.writeInt(vector.length);
                for (float v : vector) {
                    dos.writeFloat(v);
                }

                // Write metadata as JSON
                byte[] metaBytes = entry.getMetadata() != null
                        ? OBJECT_MAPPER.writeValueAsBytes(entry.getMetadata())
                        : new byte[0];
                dos.writeInt(metaBytes.length);
                if (metaBytes.length > 0) {
                    dos.write(metaBytes);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to save vector store as binary: " + path, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void loadFromBinary(VectorStore<T> store, String path) {
        try (FileInputStream fis = new FileInputStream(path);
             DataInputStream dis = new DataInputStream(fis)) {

            int magic = dis.readInt();
            if (magic != MAGIC) {
                throw new IOException("Invalid file format: bad magic number");
            }

            int version = dis.readInt();
            if (version != VERSION) {
                throw new IOException("Unsupported version: " + version);
            }

            int count = dis.readInt();
            for (int i = 0; i < count; i++) {
                // Read ID
                int idLen = dis.readInt();
                byte[] idBytes = new byte[idLen];
                dis.readFully(idBytes);
                String id = new String(idBytes, StandardCharsets.UTF_8);

                // Read vector
                int dim = dis.readInt();
                float[] vector = new float[dim];
                for (int d = 0; d < dim; d++) {
                    vector[d] = dis.readFloat();
                }

                // Read metadata (as JSON)
                int metaLen = dis.readInt();
                byte[] metaBytes = new byte[metaLen];
                dis.readFully(metaBytes);
                T metadata = null;
                if (metaLen > 0) {
                    metadata = OBJECT_MAPPER.readValue(
                            metaBytes, new TypeReference<T>() {});
                }

                store.add(id, vector, metadata);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load vector store from binary: " + path, e);
        }
    }

    /**
     * Encodes a float array as a base64 string for JSON storage.
     */
    public static String encodeVector(float[] vector) {
        ByteBuffer buf = ByteBuffer.allocate(vector.length * 4);
        for (float v : vector) {
            buf.putFloat(v);
        }
        return Base64.getEncoder().encodeToString(buf.array());
    }

    /**
     * Decodes a base64 string back to a float array.
     */
    public static float[] decodeVector(String encoded) {
        byte[] bytes = Base64.getDecoder().decode(encoded);
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        float[] vector = new float[bytes.length / 4];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = buf.getFloat();
        }
        return vector;
    }
}
