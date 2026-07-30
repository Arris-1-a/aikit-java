package com.nousresearch.aikit.prompt.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Jinja2-like template engine for prompt strings.
 *
 * <p>Supports variable substitution, conditionals, and loops
 * using a lightweight template syntax:</p>
 *
 * <ul>
 *   <li><b>Variables:</b> {@code {{ name }}} or {@code {{ user.name }}}</li>
 *   <li><b>Conditionals:</b> {@code {% if condition %}...{% endif %}}</li>
 *   <li><b>Loops:</b> {@code {% for item in items %}...{% endfor %}}</li>
 *   <li><b>Comments:</b> {@code {# this is a comment #}}</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * PromptTemplate tmpl = PromptTemplate.compile(
 *     "Hello {{ name }}, you have {{ items.size() }} items.");
 * String result = tmpl.render(Map.of("name", "Alice", "items", List.of(1,2,3)));
 * }</pre>
 */
public class PromptTemplate {

    private static final Pattern VARIABLE_PATTERN =
            Pattern.compile("\{\{\s*(.+?)\s*\}\}");
    private static final Pattern IF_PATTERN =
            Pattern.compile("\{%\s*if\s+(.+?)\s*%\}(.+?)\{%\s*endif\s*%\}", Pattern.DOTALL);
    private static final Pattern FOR_PATTERN =
            Pattern.compile("\{%\s*for\s+(\w+)\s+in\s+(.+?)\s*%\}(.+?)\{%\s*endfor\s*%\}", Pattern.DOTALL);
    private static final Pattern COMMENT_PATTERN =
            Pattern.compile("\{#.*?#\}");

    private final String template;
    private final List<String> variables;

    private PromptTemplate(String template, List<String> variables) {
        this.template = template;
        this.variables = variables;
    }

    /**
     * Compiles a template string into a reusable PromptTemplate.
     *
     * @param template the template string
     * @return a compiled template
     */
    public static PromptTemplate compile(String template) {
        List<String> vars = new ArrayList<>();
        Matcher m = VARIABLE_PATTERN.matcher(template);
        while (m.find()) {
            String varName = m.group(1).trim();
            if (!vars.contains(varName)) {
                vars.add(varName);
            }
        }
        return new PromptTemplate(template, vars);
    }

    /**
     * Renders the template with the given variable bindings.
     *
     * @param variables map of variable names to values
     * @return the rendered string
     */
    public String render(Map<String, Object> variables) {
        String result = template;

        // Remove comments
        result = COMMENT_PATTERN.matcher(result).replaceAll("");

        // Process for loops
        result = processForLoops(result, variables);

        // Process if conditionals
        result = processIfBlocks(result, variables);

        // Substitute variables
        result = substituteVariables(result, variables);

        return result;
    }

    /**
     * Convenience method: compile and render in one call.
     */
    public static String render(String template, Map<String, Object> variables) {
        return compile(template).render(variables);
    }

    private String substituteVariables(String text, Map<String, Object> vars) {
        Matcher m = VARIABLE_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String varExpr = m.group(1).trim();
            Object value = resolveVariable(varExpr, vars);
            m.appendReplacement(sb, Matcher.quoteReplacement(
                    value != null ? value.toString() : ""));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private String processIfBlocks(String text, Map<String, Object> vars) {
        Matcher m = IF_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String condition = m.group(1).trim();
            String body = m.group(2);
            boolean truthy = evaluateCondition(condition, vars);
            m.appendReplacement(sb, Matcher.quoteReplacement(truthy ? body : ""));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private String processForLoops(String text, Map<String, Object> vars) {
        Matcher m = FOR_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String varName = m.group(1).trim();
            String iterExpr = m.group(2).trim();
            String body = m.group(3);

            Object iterable = resolveVariable(iterExpr, vars);
            StringBuilder loopResult = new StringBuilder();

            if (iterable instanceof Iterable) {
                for (Object item : (Iterable<?>) iterable) {
                    Map<String, Object> loopVars = new java.util.HashMap<>(vars);
                    loopVars.put(varName, item);
                    loopResult.append(substituteVariables(body, loopVars));
                }
            } else if (iterable instanceof Object[]) {
                for (Object item : (Object[]) iterable) {
                    Map<String, Object> loopVars = new java.util.HashMap<>(vars);
                    loopVars.put(varName, item);
                    loopResult.append(substituteVariables(body, loopVars));
                }
            }

            m.appendReplacement(sb, Matcher.quoteReplacement(loopResult.toString()));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Resolves a dotted variable expression like "user.name".
     */
    @SuppressWarnings("unchecked")
    private Object resolveVariable(String expr, Map<String, Object> vars) {
        String[] parts = expr.split("\.");
        Object current = vars;

        for (String part : parts) {
            // Handle method calls like items.size()
            String fieldName = part;
            String methodCall = null;
            int parenIdx = part.indexOf('(');
            if (parenIdx > 0 && part.endsWith(")")) {
                fieldName = part.substring(0, parenIdx);
                methodCall = part.substring(parenIdx + 1, part.length() - 1);
            }

            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(fieldName);
            } else if (current != null) {
                // Try reflection for POJO field access
                try {
                    String getter = "get" + Character.toUpperCase(fieldName.charAt(0))
                            + fieldName.substring(1);
                    java.lang.reflect.Method method = current.getClass().getMethod(getter);
                    current = method.invoke(current);
                } catch (Exception e) {
                    return null;
                }

                if (methodCall != null && !methodCall.isEmpty() && current != null) {
                    try {
                        java.lang.reflect.Method method = current.getClass()
                                .getMethod(fieldName);
                        current = method.invoke(current);
                    } catch (Exception e) {
                        return null;
                    }
                }
            } else {
                return null;
            }
        }

        return current;
    }

    /**
     * Evaluates a condition expression.
     */
    private boolean evaluateCondition(String condition, Map<String, Object> vars) {
        if (condition == null || condition.isEmpty()) return false;

        // Handle negation
        if (condition.startsWith("!")) {
            return !evaluateCondition(condition.substring(1).trim(), vars);
        }

        // Handle "not"
        if (condition.startsWith("not ")) {
            return !evaluateCondition(condition.substring(4).trim(), vars);
        }

        // Handle comparisons
        String[] operators = {"==", "!=", ">=", "<=", ">", "<"};
        for (String op : operators) {
            int idx = condition.indexOf(op);
            if (idx > 0) {
                String left = condition.substring(0, idx).trim();
                String right = condition.substring(idx + op.length()).trim();
                Object leftVal = resolveVariable(left, vars);
                Object rightVal = resolveVariable(right, vars);
                if (leftVal == null && rightVal == null) {
                    return op.equals("==");
                }
                return compareValues(leftVal, rightVal, op);
            }
        }

        // Truthiness check
        Object val = resolveVariable(condition, vars);
        return val != null && !Boolean.FALSE.equals(val)
                && (!(val instanceof String) || !((String) val).isEmpty())
                && (!(val instanceof Number) || ((Number) val).doubleValue() != 0);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean compareValues(Object left, Object right, String op) {
        if (left == null || right == null) {
            return "!=".equals(op);
        }
        int cmp;
        if (left instanceof Comparable && right instanceof Comparable) {
            cmp = ((Comparable) left).compareTo(right);
        } else {
            cmp = left.toString().compareTo(right.toString());
        }
        switch (op) {
            case "==": return cmp == 0;
            case "!=": return cmp != 0;
            case ">": return cmp > 0;
            case "<": return cmp < 0;
            case ">=": return cmp >= 0;
            case "<=": return cmp <= 0;
            default: return false;
        }
    }

    /** @return the raw template string */
    public String getTemplate() { return template; }

    /** @return list of variable names used in the template */
    public List<String> getVariables() { return variables; }

    @Override
    public String toString() {
        return "PromptTemplate{vars=" + variables + "}";
    }
}
