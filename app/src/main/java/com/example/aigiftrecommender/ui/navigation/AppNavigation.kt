package com.example.aigiftrecommender.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aigiftrecommender.ui.screens.contacts.ContactListScreen
import com.example.aigiftrecommender.ui.screens.home.HomeScreen
import com.example.aigiftrecommender.ui.screens.settings.SettingsScreen
// Import other screens as they are created
// import com.example.aigiftrecommender.ui.screens.gifts.GiftIdeasScreen
// import com.example.aigiftrecommender.ui.screens.contacts.ContactDetailScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Contacts : Screen("contacts")
    // object ContactDetail : Screen("contact_detail/{contactId}") {
    //    fun createRoute(contactId: String) = "contact_detail/$contactId"
    // }
    object Settings : Screen("settings")
    // object GiftIdeas : Screen("gift_ideas/{contactId}/{eventId}") { // eventId could be birthday or holiday name
    //    fun createRoute(contactId: String, eventId: String) = "gift_ideas/$contactId/$eventId"
    // }
    object ApiKeySetup : Screen("api_key_setup")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route // Or Screen.ApiKeySetup.route if key not present
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Contacts.route) {
            ContactListScreen(navController = navController)
        }
        // composable(
        //     Screen.ContactDetail.route,
        //     arguments = listOf(navArgument("contactId") { type = NavType.StringType })
        // ) {
        //     val contactId = it.arguments?.getString("contactId")
        //     if (contactId != null) {
        //         ContactDetailScreen(navController = navController, contactId = contactId)
        //     }
        // }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        // composable(
        //     Screen.GiftIdeas.route,
        //     arguments = listOf(
        //         navArgument("contactId") { type = NavType.StringType },
        //         navArgument("eventId") { type = NavType.StringType }
        //     )
        // ) {
        //     val contactId = it.arguments?.getString("contactId")
        //     val eventId = it.arguments?.getString("eventId")
        //     if (contactId != null && eventId != null) {
        //         GiftIdeasScreen(navController = navController, contactId = contactId, eventId = eventId)
        //     }
        // }
        composable(Screen.ApiKeySetup.route) {
            // ApiKeySetupScreen(navController = navController)
        }
    }
}

