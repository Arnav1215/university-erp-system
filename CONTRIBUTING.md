# Contributing to University ERP System

Thank you for your interest in contributing to the University ERP System! This document provides guidelines for contributing to this project.

## 🚀 Getting Started

1. Fork the repository on GitHub
2. Clone your fork locally
3. Create a new branch for your feature or bug fix
4. Make your changes
5. Test your changes thoroughly
6. Submit a pull request

## 📋 Development Setup

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Local Development
1. Clone the repository
2. Set up the database using the provided SQL scripts
3. Configure `config.properties` with your database credentials
4. Run `mvn clean compile` to build the project
5. Use `mvn exec:java -Dexec.mainClass="edu.univ.erp.ui.MainApp"` to run

## 🎯 How to Contribute

### Reporting Bugs
- Use the GitHub issue tracker
- Provide detailed information about the bug
- Include steps to reproduce
- Mention your environment (OS, Java version, etc.)

### Suggesting Features
- Open an issue with the "enhancement" label
- Describe the feature and its benefits
- Provide use cases and examples

### Code Contributions
- Follow the existing code style and conventions
- Write clear, concise commit messages
- Include tests for new functionality
- Update documentation as needed

## 📝 Code Style Guidelines

### Java Conventions
- Use 4 spaces for indentation
- Follow standard Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Keep methods focused and concise

### Database Guidelines
- Use descriptive table and column names
- Follow normalization principles
- Include appropriate indexes
- Use foreign key constraints

### UI Guidelines
- Follow the existing theme system (ERPColors, ERPFonts)
- Ensure responsive design
- Provide user feedback for actions
- Handle errors gracefully

## 🧪 Testing

- Test your changes thoroughly before submitting
- Include unit tests for new functionality
- Test with different user roles (Admin, Instructor, Student)
- Verify database operations work correctly

## 📚 Documentation

- Update README.md if needed
- Add inline comments for complex logic
- Update API documentation
- Include examples for new features

## 🔄 Pull Request Process

1. Ensure your code follows the style guidelines
2. Update documentation as needed
3. Add tests for new functionality
4. Ensure all tests pass
5. Create a clear pull request description
6. Link to any related issues

### Pull Request Template
```
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Manual testing completed
- [ ] Database operations verified

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] Tests added/updated
```

## 🏷️ Issue Labels

- `bug`: Something isn't working
- `enhancement`: New feature or request
- `documentation`: Improvements to documentation
- `good first issue`: Good for newcomers
- `help wanted`: Extra attention needed

## 📞 Getting Help

- Open an issue for questions
- Check existing issues and documentation
- Contact maintainers for complex questions

## 🙏 Recognition

Contributors will be recognized in the project documentation and release notes.

Thank you for contributing to the University ERP System!