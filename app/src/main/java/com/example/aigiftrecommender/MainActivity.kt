package com.example.aigiftrecommender

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.aigiftrecommender.ui.navigation.AppNavigation
import com.example.aigiftrecommender.ui.theme.AIGiftRecommenderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission results if needed, e.g., show a message if denied
        // For now, we assume permissions are granted for simplicity in this stage
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIGiftRecommenderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RequestPermissionsScreen {
                        AppNavigation() // Main app navigation starts here
                    }
                }
            }
        }
    }

    @Composable
    fun RequestPermissionsScreen(content: @Composable () -> Unit) {
        val context = LocalContext.current
        var allPermissionsGranted by remember { mutableStateOf(checkInitialPermissions()) }

        LaunchedEffect(Unit) {
            if (!allPermissionsGranted) {
                val permissionsToRequest = mutableListOf<String>()
                permissionsToRequest.add(Manifest.permission.READ_CONTACTS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                }
                // INTERNET is usually granted by default
                // RECEIVE_BOOT_COMPLETED is for a BroadcastReceiver, handled differently

                if (permissionsToRequest.isNotEmpty()) {
                    requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
                }
            }
        }
        // This is a simplified check. In a real app, you would update `allPermissionsGranted`
        // based on the `requestPermissionLauncher` callback and guide the user if denied.
        // For now, we proceed to content, assuming permissions will be granted or handled by OS.
        content()
    }

    private fun checkInitialPermissions(): Boolean {
        val context = this
        val contactsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        val notificationsPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not needed for older versions
        }
        return contactsPermission && notificationsPermission
    }
}

