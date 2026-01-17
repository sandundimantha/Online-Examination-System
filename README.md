# SmartExam - Online Examination System (Android)

## Project Overview
SmartExam is a native Android application developed in Kotlin using the MVVM architecture. It facilitates online examinations with role-based access for Students and Admins.

## Features
- **Authentication**: Secure Login and Registration using Firebase Auth.
- **Roles**: Distinct flows for Students (Take Exam) and Admins (Create Exam).
- **Exam Engine**: MCQ based exams with real-time countdown timer.
- **Auto Marking**: Instant result calculation and feedback (Pass/Fail).
- **Admin Dashboard**: Create exams, add questions, and view student results.
- **Results & Analytics**: Track performance with scores and percentage.

## Technologies Used
- **Language**: Kotlin
- **UI**: XML Layouts
- **Architecture**: MVVM (Model-View-ViewModel)
- **Backend / DB**: Firebase Realtime Database & Authentication
- **Asynchronous**: Coroutines & LiveData

## Installation Steps
1.  **Open in Android Studio**:
    - Select "Open an existing Android Studio project" and navigate to this folder.
2.  **Firebase Setup**:
    - Create a project in the [Firebase Console](https://console.firebase.google.com/).
    - Add an Android App with package name `com.example.smartexam`.
    - Download `google-services.json` and place it in the `app/` directory (next to `app/build.gradle.kts`).
    - Enable **Authentication** (Email/Password).
    - Enable **Realtime Database** (Start in Test Mode).
3.  **Sync Gradle**:
    - Allow Android Studio to download dependencies.
4.  **Run App**:
    - Connect a device or start an Emulator.
    - Click `Run`.

## Future Improvements
- Randomized questions pool.
- Negative marking support.
- Statistical charts for student performance history.
- Web-based Admin Panel.

## Screenshots
*(Add screenshots here after running the app)*
