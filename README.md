# 💳 WalletFlow - Expense & Budget Tracker

**WalletFlow** is a premium, modern, and high-fidelity Android application built entirely with **Jetpack Compose (Material 3)**. It is designed to help users track transactions, schedule and organize budgets with smart threshold notifications, analyze spending patterns with interactive visual charts, and secure data locally using PIN codes and device biometric authentication.

---

## ✨ Features

### 1. 📊 Interactive Dashboard
*   **Linear Gradient Hero Card**: Displays total monthly spending and daily averages with styled visual gradients.
*   **Quick Insights**: Track active budgets, transaction counts, and utilization rates.
*   **Recent Activity**: Instant overview of the latest five transactions with visual categories.

### 2. 💸 Smart Budgets & Limit Warnings
*   **Flexible Periods**: Plan weekly, monthly, quarterly, or yearly budgets.
*   **Visual Utilization Tracks**: Color-coded progress bars (Green for normal, Orange for warning limit, and Red for over-budget).
*   **Real-time Calculations**: Automatic calculation of current spending, remaining balance, and days remaining.

### 3. 🔍 Searchable Transaction Logs
*   **Dynamic Search**: Real-time filtering by transaction title, notes, or category.
*   **Details & Edit**: Simple interfaces to add, modify, or delete logs.
*   **Horizontal category scroll**: Cohesive chip-selection rows matching the core visual guidelines.

### 4. 🔏 High-Security Local Protection
*   **Custom PIN Pad**: A custom, responsive numeric keypad overlay on app startup.
*   **Biometric Bypass**: Native integration of Fingerprint and FaceID bypass locally.
*   **Automatic Lock**: Secures data instantly when the application goes to the background.

### 5. 🏷️ Category Management
*   **Visual Customization**: Transparent action headers, soft category emoji badges, and custom indicators.
*   **Details Hero Preview**: Modern centered card overlays highlighting active statuses and soon-coming visual configs.

### 6. 📈 Visual Analytics
*   **Interactive Charts**: Integrated YCharts library showing weekly spending distributions and category breakdowns.
*   **Dynamic Theme Matching**: Charts adapt instantly between light and dark modes.

### 7. 🔔 Background Notification Alerts
*   **Periodic Worker**: Custom `BudgetAlertWorker` powered by WorkManager running on a 4-hour cycle.
*   **Push Notifications**: Fires local system alerts when spending reaches 80% or goes over-budget.
*   **Runtime Permissions**: Full Android 13+ support with runtime dialog triggers.

### 8. 📁 Export & Reporting
*   **CSV Exports**: Export transactions and budget limits to clean CSV formats.
*   **PDF Reports**: Generate detailed PDF summaries and share them instantly using secure FileProviders.

---

## 🛠️ Architecture & Tech Stack

WalletFlow follows **MVVM (Model-View-ViewModel)** and **Clean Architecture** patterns, ensuring a testable, maintainable, and modular codebase.

*   **UI Framework**: Jetpack Compose (Material 3)
*   **Navigation**: Jetpack Navigation Compose
*   **Dependency Injection**: Dagger Hilt (`hilt-android`, `hilt-work`, `hilt-navigation-compose`)
*   **Database**: Room DB (SQLite) with KSP compilation
*   **Background Jobs**: WorkManager (KTX + Hilt integration)
*   **Security**: Android Biometric API
*   **Charts**: YCharts (co.yml:ycharts)
*   **Coroutines**: Kotlin Coroutines & Flow
*   **Compiler Toolchain**: Kotlin Symbol Processing (KSP) + Java 17

---

## 📁 Project Structure

```
app/src/main/java/com/example/expensetracker/
├── ExpenseTrackerApplication.kt      # App entry point, creates channels & schedules workers
├── MainActivity.kt                  # Main launcher activity, requests POST_NOTIFICATIONS
├── data/
│   ├── local/
│   │   ├── dao/                     # Room DAOs (ExpenseDao, BudgetDao)
│   │   ├── database/                # Database class and migrations
│   │   ├── entity/                  # Database schema entities (ExpenseEntity, BudgetEntity)
│   │   └── preferences/             # SharedPreferences/Theme configurations
│   └── repository/                  # Repository implementations
├── di/
│   └── AppModule.kt                 # Dagger Hilt dependency modules
├── domain/
│   ├── model/                       # Core domain models (Expense, Budget, BudgetAlert)
│   ├── repository/                  # Domain-level repository interfaces
│   ├── service/                     # CSV & PDF export service contracts
│   └── usecase/                     # Modular business logic use-cases
├── presentation/
│   ├── component/                   # Reusable components (Shimmer skeletons, charts)
│   ├── navigation/                  # BottomNavItem definitions and NavHost routing
│   ├── screen/                      # Screen layouts (Dashboard, Budgets, Auth)
│   └── viewmodel/                   # Stateholders handling UI actions
├── ui/
│   └── theme/                       # Material 3 ColorScheme typography and styling
└── worker/
    └── BudgetAlertWorker.kt         # WorkManager job checking thresholds & firing alerts
```

---

## 🚀 Setup & Installation Instructions

### Prerequisites
*   **Java**: JDK 17
*   **Android Studio**: Hedgehog or newer

### Building the Project
Clone the repository and compile using the Gradle wrapper commands:

1.  **Verify Kotlin Compilation**:
    ```bash
    ./gradlew compileDebugKotlin
    ```
2.  **Generate Runnable Debug APK**:
    ```bash
    ./gradlew assembleDebug
    ```
    Once completed, the build will be available at:
    `app/build/outputs/apk/debug/app-debug.apk`

---

## ✍️ Authors & Credits
*   **Bilash Kumar Biswas** — Lead Android Developer
