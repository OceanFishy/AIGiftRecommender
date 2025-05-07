# AI Gift Recommender Android Application

This document provides instructions on how to build and run the AI Gift Recommender Android application.

## Project Overview

The application helps users find personalized gift ideas for their contacts using the ChatGPT API. It features:
- Access to Android contacts (birthdays, notes).
- AI-powered gift recommendations via ChatGPT.
- Shopping links (Amazon search) for suggested gifts.
- Customizable holiday list for reminders.
- Notification system for upcoming birthdays and holidays (2 weeks in advance).
- Secure API key management for ChatGPT.

## Prerequisites

1.  **Android Studio:** Ensure you have the latest stable version of Android Studio installed (e.g., Iguana | 2023.2.1 or newer).
2.  **Android SDK:** Make sure you have the Android SDK Platform for API level 34 (or the `compileSdk` version specified in `app/build.gradle`) installed via Android Studio's SDK Manager.
3.  **Kotlin Plugin:** Ensure the Kotlin plugin is enabled in Android Studio (usually enabled by default).
4.  **ChatGPT API Key:** You will need a valid API key from OpenAI to use the gift recommendation feature.

## Project Structure

The project is structured as a standard Android application with the following key directories and files:

-   `/AIGiftRecommender/app/src/main/java/com/example/aigiftrecommender/`: Contains all Kotlin source code (data models, repositories, ViewModels, UI screens, services, etc.).
-   `/AIGiftRecommender/app/src/main/res/`: Contains all Android resources (layouts, drawables, values like strings.xml, etc.).
-   `/AIGiftRecommender/app/AndroidManifest.xml`: Application manifest file defining permissions and components.
-   `/AIGiftRecommender/build.gradle`: Project-level Gradle build script.
-   `/AIGiftRecommender/app/build.gradle`: App-module-level Gradle build script with dependencies.

## Build and Run Instructions

1.  **Download and Extract:**
    *   Download the provided `AIGiftRecommender_Project.zip` file.
    *   Extract the contents to a local directory on your computer.

2.  **Open in Android Studio:**
    *   Open Android Studio.
    *   Select "Open" or "Open an Existing Project".
    *   Navigate to the extracted `AIGiftRecommender` directory and select it.
    *   Android Studio will import the project and Gradle will sync the dependencies. This may take a few minutes.

3.  **Configure ChatGPT API Key:**
    *   Once the project is open and synced, run the application on an emulator or a physical Android device.
    *   Navigate to the **Settings** screen within the app.
    *   Tap on "Set API Key" (or "Update API Key").
    *   Enter your valid OpenAI ChatGPT API key (it should start with `sk-`).
    *   Save the key. The app will store it securely for future use.
    *   **Note:** Without a valid API key, the gift recommendation feature will not work.

4.  **Grant Permissions:**
    *   On the first launch, the app will request permission to access your contacts. This is necessary to read birthdays and notes for personalized recommendations. Please grant this permission.
    *   If you are on Android 13 or higher, the app will also request permission to send notifications. Please grant this to receive reminders.

5.  **Build and Run:**
    *   Select a target device (emulator or physical device) from the dropdown menu in Android Studio.
    *   Click the "Run" button (green play icon) or select "Run" > "Run 'app'" from the menu.
    *   Android Studio will build the APK and install it on the selected device.

## Key Features to Test

*   **Contact Access:** Verify that the app lists your contacts and can access their details (birthdays, notes if available).
*   **Holiday Management:** Check the Settings screen to manage which holidays you want reminders for.
*   **Gift Recommendations:** Select a contact and an event (birthday/holiday) to get gift ideas. Ensure the ChatGPT API is called and results are displayed.
*   **Shopping Links:** Tapping on a gift idea should generate an Amazon search link.
*   **Notifications:** (This may require setting device time or waiting) Verify that notifications for upcoming birthdays/holidays (14 days in advance) are triggered. You can test the `ReminderWorker` more directly with shorter intervals in `MainApplication.kt` for development purposes.
*   **API Key Management:** Ensure the API key can be set and updated correctly.

## Troubleshooting

*   **Gradle Sync Issues:** Ensure your Android Studio is up to date and you have a stable internet connection. Try "File" > "Sync Project with Gradle Files". Check the `build.gradle` files for any version conflicts if errors persist.
*   **API Errors:** If you encounter errors related to the ChatGPT API:
    *   Double-check that your API key is correct and has not expired or exceeded its quota.
    *   Ensure your device has internet connectivity.
    *   The error messages from the API should provide some insight.
*   **Permissions Denied:** If features are not working, ensure the app has the necessary permissions (Contacts, Notifications) via the device's app settings.

## Included Documentation Files

For your reference, the following planning and design documents are included in the project's root directory (though they are also part of the source code generation process you observed):

*   `app_requirements.md`: Detailed application requirements.
*   `tech_stack_and_architecture.md`: Chosen technology stack and architectural design.
*   `app_structure_user_flow.md`: App screen structure and user flow diagrams.
*   `permissions_and_data_handling.md`: Details on Android permissions and data privacy.
*   `todo.md`: The checklist used during development.

We hope you find this application useful! If you have any questions regarding the build process, please refer to the Android Studio documentation or common Android development resources.
