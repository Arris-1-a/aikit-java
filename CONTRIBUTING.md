# Contributing to AiKit Java

Thank you for your interest in contributing! We welcome all contributions.

## Development Setup

1. **Fork and clone** the repository
2. **Install JDK 17+** (Eclipse Temurin recommended)
3. **Build** the project: `./gradlew build`

## Code Style

- Follow standard Java conventions (Effective Java style)
- Run Checkstyle before committing: `./gradlew checkstyleMain`
- Run SpotBugs: `./gradlew spotbugsMain`
- Write Javadoc for all public APIs

## Testing

- Write JUnit 5 tests for new features
- Run tests: `./gradlew test`
- Aim for >80% coverage on new code

## Pull Request Process

1. Create a feature branch: `git checkout -b feature/my-feature`
2. Make your changes with clear commit messages
3. Ensure all checks pass (Checkstyle, SpotBugs, tests)
4. Submit a PR against the `main` branch
5. Describe what changed and why

## Commit Guidelines

- Use present tense ("Add feature" not "Added feature")
- Reference issue numbers: `Fixes #123`

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
