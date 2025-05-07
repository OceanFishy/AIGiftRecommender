# App Structure and User Flow Design

This document details the application structure, main screens, and user flow for the AI Gift Recommendation Android app, adhering to Material Design 3 principles and an MVVM architecture.

## 1. Overall App Structure & Navigation

The app will utilize a Bottom Navigation Bar for primary navigation between main sections. A Toolbar/App Bar will be present on most screens for context and actions.

**Main Navigation Sections (Bottom Navigation Bar):**

1.  **Home/Upcoming:** Displays upcoming birthdays and holidays, and quick access to contacts needing gift ideas.
2.  **Contacts:** Lists all contacts, allowing users to view details and get gift recommendations.
3.  **Settings:** Allows users to manage preferences, API key, and holiday selections.

## 2. Screen Definitions and User Flows

### 2.1. Splash Screen / Initial Setup

*   **Purpose:** App launch, permission requests (Contacts).
*   **Flow:**
    1.  App Launch: Display splash screen (app logo/name).
    2.  Permission Check: Check for Contacts permission.
        *   If not granted: Display a rationale and prompt user to grant permission. If denied, show a message explaining the app cannot function without it and offer to retry or exit.
        *   If granted: Proceed to API Key Check.
    3.  API Key Check (after permissions):
        *   Check if ChatGPT API key is configured (e.g., in encrypted SharedPreferences or Room).
        *   If not configured: Navigate to a dedicated API Key Setup screen (can be part of Settings, but shown modally or as a first-run step).
        *   If configured: Navigate to the Home/Upcoming screen.

### 2.2. Home / Upcoming Screen

*   **Purpose:** Provide an overview of upcoming events (birthdays, holidays) and quick access to gift ideas.
*   **UI Elements (Material Design 3):**
    *   App Bar with app title.
    *   Cards or list items for "Upcoming Birthdays" (e.g., within next 30 days).
        *   Each item shows: Contact Name, Birthday Date, Days Remaining.
        *   Action: Tap to navigate to Contact Gift Ideas Screen for that contact and birthday.
    *   Cards or list items for "Upcoming Selected Holidays."
        *   Each item shows: Holiday Name, Date, Days Remaining.
        *   Action: Tap to navigate to a Holiday Gift Ideas Screen (could show general ideas or prompt to select a contact for this holiday).
    *   A section for "Needs Gift Ideas" (contacts whose events are approaching soon and no ideas have been viewed/saved).
    *   Floating Action Button (FAB) (Optional): Quick add contact or event (if manual entry is prioritized).
*   **User Flow:**
    1.  User lands on this screen after setup.
    2.  Views upcoming birthdays and holidays.
    3.  Taps on a birthday -> Navigates to Contact Gift Ideas Screen.
    4.  Taps on a holiday -> Navigates to Holiday Gift Ideas Screen.

### 2.3. Contacts List Screen

*   **Purpose:** Allow users to browse all their contacts and initiate gift recommendations.
*   **UI Elements:**
    *   App Bar with screen title ("Contacts") and a search icon.
    *   List of contacts (RecyclerView or LazyColumn in Compose).
        *   Each item shows: Contact Name, (Optional: next birthday if soon).
        *   Action: Tap to navigate to Contact Details Screen.
    *   Search bar (activated by search icon) to filter contacts by name.
*   **User Flow:**
    1.  User navigates via Bottom Navigation.
    2.  Scrolls through or searches for a contact.
    3.  Taps on a contact -> Navigates to Contact Details Screen.

### 2.4. Contact Details Screen

*   **Purpose:** Display details for a selected contact and provide options for gift recommendations.
*   **UI Elements:**
    *   App Bar with contact name.
    *   Contact Information section: Birthday (if available), Notes (if available, summarized or expandable).
    *   Button: "Get Birthday Gift Ideas" (if birthday exists).
    *   Button: "Get Gift Ideas for Holiday..." (prompts to select a holiday from user's list).
    *   Option to manually add/edit interests for this contact (if not in notes).
*   **User Flow:**
    1.  User arrives from Contacts List Screen.
    2.  Views contact details.
    3.  Taps "Get Birthday Gift Ideas" -> Navigates to Contact Gift Ideas Screen (pre-filled for birthday).
    4.  Taps "Get Gift Ideas for Holiday..." -> Shows a dialog to select a holiday -> Navigates to Contact Gift Ideas Screen (pre-filled for contact and selected holiday).

### 2.5. Contact Gift Ideas Screen

*   **Purpose:** Display AI-generated gift ideas for a specific contact and event (birthday/holiday).
*   **UI Elements:**
    *   App Bar with title like "Gifts for [Contact Name] - [Event]".
    *   Loading indicator while fetching ideas from ChatGPT API.
    *   List of gift ideas (Cards or list items).
        *   Each item shows: Gift Idea Title/Name, Short Description, Amazon Shopping Link (button or icon).
        *   (Optional: Save/Favorite gift idea).
    *   "No ideas found" or error message if API fails or returns no relevant suggestions.
    *   Refresh button to try generating ideas again.
*   **User Flow:**
    1.  User arrives from Home Screen (tapping an upcoming event) or Contact Details Screen.
    2.  App makes API call to ChatGPT (using contact notes, event details).
    3.  Loading state is shown.
    4.  Gift ideas are displayed.
    5.  User browses ideas.
    6.  User taps a shopping link -> Opens Amazon search URL in a browser (or in-app browser view).

### 2.6. Holiday Gift Ideas Screen (General or for a Contact)

*   **Purpose:** Display AI-generated gift ideas for a specific holiday.
*   **UI Elements:** Similar to Contact Gift Ideas Screen, but context is a holiday.
    *   If navigated from Home (general holiday), might show general ideas or prompt to select a contact.
    *   If for a specific contact (via Contact Details), similar to Contact Gift Ideas Screen.
*   **User Flow:** Similar to Contact Gift Ideas Screen.

### 2.7. Settings Screen

*   **Purpose:** Allow user to configure the app.
*   **UI Elements (List of preference items):**
    *   **ChatGPT API Key:**
        *   Displays current status (e.g., "API Key Set" or "API Key Not Set").
        *   Action: Tap to open a dialog/screen to input/update the API key. Input should be masked.
    *   **Manage Holidays:**
        *   Action: Tap to navigate to a Holiday Management Screen.
        *   Displays a list of common major holidays with checkboxes for users to select/deselect.
        *   Option to add a custom holiday (name, date).
    *   **Notification Preferences (Optional granular control):**
        *   Toggle for enabling/disabling all gift notifications.
        *   (Advanced: Set reminder lead time if different from default 2 weeks).
    *   **About/Privacy Policy:** Links to relevant information.
*   **User Flow:**
    1.  User navigates via Bottom Navigation.
    2.  Taps "ChatGPT API Key" -> Enters/updates key.
    3.  Taps "Manage Holidays" -> Selects/deselects holidays, adds custom ones.

### 2.8. Notification Flow

*   **Trigger:** Scheduled background task (WorkManager) runs periodically (e.g., daily).
*   **Logic:**
    1.  Task checks for contacts' birthdays occurring in exactly 14 days.
    2.  Task checks for user-selected holidays occurring in exactly 14 days.
    3.  For each upcoming event, a notification is created.
*   **Notification Content:**
    *   Title: e.g., "[Contact Name]'s Birthday is in 2 weeks!"
    *   Text: e.g., "Tap to get gift ideas."
    *   Action: Tapping the notification opens the app and navigates directly to the Contact Gift Ideas Screen for that contact/event.

## 3. User Flow Diagrams (Conceptual - Text-Based)

**A. New User Setup & First Gift Idea:**

`App Launch` -> `Splash` -> `Permission Request (Contacts)`
  `└─ (Granted)` -> `API Key Check`
    `└─ (Not Set)` -> `API Key Setup Screen` -> `(Key Entered)` -> `Home Screen`
    `└─ (Set)` -> `Home Screen`
      `└─ Tap Upcoming Birthday` -> `Contact Gift Ideas Screen (Loading)` -> `(Ideas Displayed)`
        `└─ Tap Shopping Link` -> `Browser (Amazon)`

**B. Existing User - Find Gift for Contact:**

`App Launch` -> `Home Screen` -> `Bottom Nav: Contacts` -> `Contacts List Screen`
  `└─ Tap Contact` -> `Contact Details Screen`
    `└─ Tap "Get Birthday Gift Ideas"` -> `Contact Gift Ideas Screen (Loading)` -> `(Ideas Displayed)`

**C. Manage Settings:**

`App Launch` -> `Home Screen` -> `Bottom Nav: Settings` -> `Settings Screen`
  `└─ Tap "Manage Holidays"` -> `Holiday Management Screen` -> `(Select/Deselect Holidays)` -> `Back to Settings`
  `└─ Tap "ChatGPT API Key"` -> `API Key Input Dialog` -> `(Enter Key)` -> `Back to Settings`

This structure and flow aim for an intuitive user experience, leveraging Material Design components for a familiar Android look and feel, while supporting all the core functionalities of the AI gift recommendation engine.
