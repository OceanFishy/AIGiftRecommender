# Android Permissions and Data Handling

This document outlines the necessary Android permissions, data handling practices, and privacy considerations for the AI Gift Recommendation application.

## 1. Required Android Permissions

The application will require the following Android permissions to function correctly:

*   **`android.permission.READ_CONTACTS`**
    *   **Purpose:** To access contact names, birthday information, and notes associated with contacts. This data is essential for personalizing gift recommendations.
    *   **Rationale to User:** "This app needs access to your contacts to find birthdays and read notes, which helps in generating personalized gift ideas for your friends and family."

*   **`android.permission.INTERNET`**
    *   **Purpose:** To communicate with the ChatGPT API for generating gift suggestions and to construct search URLs for Amazon (or other retailers) for shopping links.
    *   **Rationale to User:** "Internet access is required to connect to the AI service for gift ideas and to find shopping links for you."

*   **`android.permission.POST_NOTIFICATIONS`** (For Android 13 API level 33 and above)
    *   **Purpose:** To display reminder notifications for upcoming birthdays and selected holidays.
    *   **Rationale to User:** "Allow notifications to get timely reminders for upcoming birthdays and holidays so you don\\u2019t miss them!"
    *   *Note:* For devices running Android versions below 13, this permission is implicitly granted when the app is installed. The app will handle API level differences for requesting this permission.

*   **`android.permission.RECEIVE_BOOT_COMPLETED`** (Recommended for robust notifications)
    *   **Purpose:** To allow the application to reschedule its background tasks (e.g., notification checks via WorkManager) after the device reboots. This ensures that reminders remain active.
    *   **Rationale to User:** (Usually handled implicitly by WorkManager, but if a separate request is needed or for transparency) "This helps ensure your gift reminders continue to work reliably even after restarting your phone."

## 2. Permission Handling Strategy

*   **Contextual Requests:** Permissions will be requested at runtime, just before they are needed for a specific feature, not all at once on app startup (except potentially core permissions like Contacts if deemed essential for initial setup flow after user consent).
*   **Clear Rationale:** Before each permission request dialog is shown, the app will display a clear and concise explanation of why the permission is needed, as outlined above.
*   **Graceful Degradation/Denial Handling:**
    *   If `READ_CONTACTS` is denied, the core functionality of the app will be severely limited. The app will inform the user that it cannot provide personalized gift ideas and may offer to re-request the permission or direct them to settings.
    *   If `INTERNET` is unavailable or denied (though typically auto-granted), API calls will fail. The app will show an error message indicating no network access.
    *   If `POST_NOTIFICATIONS` is denied, the user will not receive reminders. The app might offer a way to enable notifications later via settings.

## 3. Data Handling and Privacy

The application is designed with user privacy as a priority.

*   **Contact Data (Names, Birthdays, Notes):**
    *   Accessed locally from the device's contact provider.
    *   Only relevant information from contact notes (e.g., interests, hobbies, past gift mentions) along with birthday/event details will be used to construct prompts for the ChatGPT API.
    *   This data is used **solely** for generating personalized gift recommendations within the app.
    *   Contact data is **not** stored on any external servers controlled by this application. It is processed in memory for prompt generation and then sent to the ChatGPT API.

*   **ChatGPT API Key:**
    *   The user is required to provide their own OpenAI ChatGPT API key.
    *   The API key will be stored securely on the device using Android Keystore to encrypt it before saving it to SharedPreferences or the local Room database.
    *   The key is used **only** for authenticating requests to the ChatGPT API from the user's device.
    *   The app will provide an interface for the user to securely input and manage their API key.

*   **Data Sent to ChatGPT API:**
    *   The user has acknowledged that data (contact notes, event details) will be sent to the ChatGPT API, subject to OpenAI's data usage and privacy policies.
    *   Prompts sent to the ChatGPT API will consist of: relevant snippets from contact notes, the occasion (e.g., "birthday", "Christmas"), and potentially general interests.
    *   The app will strive to minimize the amount of personally identifiable information (PII) sent. However, the content of contact notes is user-generated and may contain PII. The user is responsible for the content of their notes.

*   **Shopping Links (Amazon):**
    *   The app will generate search query URLs for Amazon.com based on the gift ideas provided by the ChatGPT API.
    *   No personal data from the user's contacts is directly shared with Amazon by the application itself when generating these links. The link construction is based on the generic gift item description.
    *   When the user clicks a shopping link, they will be redirected to the Amazon website or app, and their interaction will then be subject to Amazon's privacy policy and terms of service.

*   **Local Storage (User Preferences):**
    *   User preferences, such as selected holidays for reminders and the encrypted ChatGPT API key, will be stored locally on the device using SharedPreferences or the Room database.
    *   This data remains on the user's device and is not transmitted elsewhere, except for the API key being used for API calls.

*   **Data Minimization:** The app will only request and use data that is strictly necessary for its intended functionality.

## 4. Security Measures

*   **HTTPS/TLS:** All communications with the ChatGPT API will be conducted over HTTPS to ensure data in transit is encrypted (Retrofit enforces this by default for `https://` URLs).
*   **API Key Security:** As mentioned, the ChatGPT API key will be encrypted using Android Keystore before local storage.
*   **Input Validation:** Basic input validation will be performed where applicable, though the primary AI interaction relies on natural language.
*   **Dependency Management:** Care will be taken in selecting and managing third-party libraries to minimize security risks (standard development practice).

## 5. Privacy Policy In-App

It is highly recommended that a concise Privacy Policy, summarizing these points, be made available within the application (e.g., in the Settings screen). This policy should clearly inform users about what data is collected, how it is used, and with whom it might be shared (i.e., OpenAI for gift generation).

By implementing these permission and data handling strategies, the AI Gift Recommendation app aims to be transparent and respectful of user privacy while delivering its core features.
