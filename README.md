# 🏠 AVIV Property App

A modern Android app for browsing aviv property listings built with **MVVM + Clean Architecture**, **Jetpack Compose**, and **Material Design 3**.
## 🎥 Demo

<div style="text-align: center;">
  <img src="media/demo.gif" alt="App demo" width="320"/>
</div>

## ✨ Features

- Browse property listings with images and details
- View detailed property information
- Smooth loading states and error handling
- Offline support with caching
- Modern Material Design 3 UI

## 🏗 Architecture

This app uses **Clean Architecture** with three layers:

- **Presentation** (UI, ViewModels, Compose)
- **Domain** (Use Cases, Business Logic)
- **Data** (API, Repository)

Each layer is separated into different modules for better organization and testability.

## 🛠 Tech Stack

- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Design system
- **MVVM** - Architecture pattern
- **Hilt** - Dependency injection
- **Retrofit** - Networking
- **Coroutines & Flow** - Async operations
- **Coil** - Image loading

## 📦 Modules

```
app/                    - Main application module
core/
  ├── common/          - Shared utilities
  ├── network/         - Networking setup
  ├── designsystem/    - Theme and design tokens
  ├── ui/              - Reusable UI components
  └── model/           - Shared models
data/                  - Data layer (API, Repository)
domain/                - Business logic (Use Cases)
feature/
  ├── listing/         - Property listing screen
  └── detail/          - Property detail screen
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1) or newer
- JDK 17+
- Android SDK 35

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/joicepj007/AvivPropertyApp.git
   ```

2. **Open in Android Studio**
   - File → Open → Select project folder
   - Wait for Gradle sync to complete

3. **Run the app**
   - Click Run button or press `Shift + F10`
   - Select emulator or device

### Build Commands

```bash
# Build debug version
./gradlew assembleDebug

# Run tests
./gradlew test

# Run code quality checks
./gradlew check
```

## 🧪 Testing

The project includes unit tests for:
- ViewModels (Presentation layer)
- Use Cases (Domain layer)
- Repositories (Data layer)

Run tests with: `./gradlew test`

## 📱 Requirements

- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)
- **Kotlin**: 2.0.20

## 📝 Code Style

- Follows [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Uses ktlint for formatting
- 4 spaces indentation
- Max line length: 120 characters

## 📄 License

MIT License - feel free to use this project for learning and development.

---

**Built with Kotlin & Jetpack Compose** ❤️
