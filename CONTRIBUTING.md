# Contributing to EasyPipe

Thank you for considering contributing to **EasyPipe**! 🎉
We welcome contributions of all kinds — from bug reports and feature requests to documentation improvements and pull requests.

---

## 📥 How to Contribute

### 🚀 Features & Ideas
- Open a [GitHub issue](https://github.com/your-org/easypipe/issues) describing the feature
- Label it as `enhancement`

### 🐞 Bug Reports
- Provide steps to reproduce
- Describe expected vs. actual behavior
- Include logs or screenshots if possible

### 🛠️ Code Contributions
1. Fork the repo
2. Create a new branch: `feature/my-feature`
3. Commit with clear messages
4. Open a PR and describe your changes

---

## 🧪 Running the Project Locally

```bash
git clone https://github.com/your-org/easypipe.git
cd easypipe
./gradlew clean build
```

To run the example project:
```bash
cd easypipe-examples
./gradlew bootRun
```

---

## ✅ Development Guidelines

- Use idiomatic Kotlin and follow Ktlint-style conventions
- Write meaningful commit messages
- Ensure unit/integration tests cover new code
- Keep changes focused and isolated
- Avoid breaking public APIs if possible

---

## 🧪 Running Tests

```bash
./gradlew test
```

Use `test` tasks from both modules:
```bash
./gradlew :easypipe-starter:test
./gradlew :easypipe-examples:test
```

---

## 🙏 Thank You

Your contributions make this project better. We’re happy to have you as part of the community!
