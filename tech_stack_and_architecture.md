# Technology Stack and Architecture Design

This document outlines the selected technology stack and high-level architecture for the AI Gift Recommendation Android application.

## 1. Technology Stack

Based on the project requirements and user preferences, the following technologies will be used:

*   **Programming Language:** Kotlin
    *   *Reasoning:* User preference and Google's recommended language for modern Android development, offering conciseness, safety, and interoperability with Java.
*   **User Interface (UI) Toolkit:** Jetpack Compose with Material Design 3
    *   *Reasoning:* User requested Material Design. Jetpack Compose is Android's modern declarative UI toolkit for building native UIs with Kotlin. Material Design 3 provides up-to-date components and theming.
*   **Architecture Pattern:** Model-View-ViewModel (MVVM)
    *   *Reasoning:* Promotes a separation of concerns, making the codebase more modular, testable, and maintainable. It works well with Jetpack Compose and LiveData/StateFlow for reactive UI updates.
*   **Asynchronous Operations:** Kotlin Coroutines
    *   *Reasoning:* Simplifies asynchronous programming, essential for network operations (API calls), database access, and other background tasks without blocking the main thread.
*   **Networking:**
    *   **ChatGPT API Integration:** Retrofit with an OkHttp client. Retrofit is a type-safe HTTP client for Android and Java, simplifying API interactions.
    *   **Amazon Shopping Links:** Initially, this will involve constructing search URLs for Amazon based on gift ideas. If a more direct integration (e.g., Amazon Product Advertising API) is feasible and the user can provide credentials, it can be explored. For now, direct URL construction is the primary approach.
*   **Data Persistence:** Room Persistence Library
    *   *Reasoning:* An abstraction layer over SQLite that allows for more robust database access while harnessing the full power of SQLite. It will be used for storing user preferences (e.g., selected holidays, API key if user inputs it) and potentially caching data for offline access or performance.
*   **Dependency Injection:** Hilt (Dagger Hilt)
    *   *Reasoning:* Simplifies dependency injection in Android applications, reducing boilerplate and improving code organization and testability.
*   **Contact Access:** Android SDK - `ContactsContract` API
    *   *Reasoning:* Standard Android API for accessing and reading contact information, including names, birthdays, and notes, with appropriate user permissions.
*   **Notification System:** Android SDK - `NotificationManager` and `WorkManager`
    *   *Reasoning:* `NotificationManager` for creating and displaying notifications. `WorkManager` for scheduling reliable background tasks, such as checking for upcoming birthdays/holidays and triggering notifications even if the app is not running.

## 2. High-Level Architecture

The application will follow the MVVM pattern with distinct layers:

*   **Presentation Layer (UI):**
    *   Implemented using Jetpack Compose.
    *   Consists of Composable functions (Views) that observe data from ViewModels.
    *   Handles user interactions and forwards them to ViewModels.
    *   Displays data provided by ViewModels (e.g., contact lists, gift suggestions, upcoming events).
    *   Adheres to Material Design 3 guidelines.
*   **ViewModel Layer:**
    *   One or more ViewModels (e.g., `ContactsViewModel`, `GiftIdeasViewModel`, `SettingsViewModel`).
    *   Contain the UI logic and expose data (using StateFlow or LiveData) to the Composables.
    *   Interact with the Data Layer (Repositories) to fetch and process data.
    *   Do not hold references to Android Framework UI components (Activities, Fragments, Views).
*   **Data Layer (Repositories & Data Sources):**
    *   **Repositories:** Act as a single source of truth for data. They abstract the data sources from the ViewModels and handle data fetching logic (e.g., deciding whether to fetch from network or local cache).
        *   `ContactsRepository`: Manages access to contact data (birthdays, notes) via `ContactsContract`.
        *   `GiftRepository`: Manages interactions with the ChatGPT API and generation of shopping links.
        *   `SettingsRepository`: Manages user preferences and holiday data stored in Room.
    *   **Data Sources:**
        *   **Remote Data Source:** ChatGPT API (via Retrofit).
        *   **Local Data Source (Contacts):** Android `ContactsContract`.
        *   **Local Data Source (Persistence):** Room Database (for user settings, holiday list, potentially API key storage).

## 3. Key Modules (Conceptual)

*   **`:app` Module:** The main application module containing UI, ViewModels, and DI setup.
*   **`:core` Module (Optional):** Could contain shared utilities, base classes, or common data models if the app grows in complexity.

## 4. Data Flow Example (Gift Recommendation)

1.  User navigates to a contact's gift recommendation screen (UI).
2.  The Composable function calls a method in its `GiftIdeasViewModel`.
3.  `GiftIdeasViewModel` requests contact details (notes, birthday) and event info (holiday) from `ContactsRepository` and `SettingsRepository`.
4.  `GiftIdeasViewModel` then calls a method in `GiftRepository` to fetch gift ideas, passing the contact info and event details.
5.  `GiftRepository` constructs a prompt and uses the Retrofit service to call the ChatGPT API.
6.  `GiftRepository` receives the response, processes it, and potentially uses a service to generate Amazon search URLs for each gift idea.
7.  `GiftRepository` returns the list of gift ideas (with descriptions and links) to `GiftIdeasViewModel`.
8.  `GiftIdeasViewModel` updates its state (e.g., a `StateFlow<List<GiftIdea>>`).
9.  The Composable UI observes this state and re-renders to display the gift recommendations.

## 5. API Key Management

*   The ChatGPT API key is sensitive. The application will provide a secure way for the user to input and store this key. Options include:
    *   Storing in SharedPreferences encrypted with Android Keystore.
    *   Storing in the Room database with appropriate encryption.
*   The key will **not** be hardcoded into the application's source code.
*   The user will be prompted to enter their API key in the app's settings if it's not already configured.

This architecture aims to create a scalable, maintainable, and testable Android application that meets all specified requirements.
