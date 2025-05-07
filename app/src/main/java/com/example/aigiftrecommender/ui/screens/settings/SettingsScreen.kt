package com.example.aigiftrecommender.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aigiftrecommender.model.Holiday
import com.example.aigiftrecommender.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val holidays by viewModel.holidays.collectAsState()
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var currentApiKey by remember { mutableStateOf("") }
    // In a real app, load the API key from secure storage via ViewModel
    LaunchedEffect(Unit) {
        viewModel.loadApiKey() // This should populate a StateFlow or similar in ViewModel
        // currentApiKey = viewModel.apiKey.value (if apiKey is a StateFlow)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Manage ChatGPT API Key", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { showApiKeyDialog = true }) {
                    Text(if (viewModel.apiKey.isNotBlank()) "Update API Key" else "Set API Key")
                }
                if (viewModel.apiKey.isNotBlank()) {
                    Text("API Key is set.", style = MaterialTheme.typography.bodySmall)
                } else {
                    Text("API Key is not set. Gift recommendations will not work.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }

            item {
                Text("Manage Holiday Preferences", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(holidays) { holiday ->
                HolidayPreferenceItem(holiday = holiday, onHolidayToggled = {
                    viewModel.updateHolidaySelection(holiday, !holiday.isSelected)
                })
            }

            // TODO: Add section for notification preferences if needed
            // TODO: Add section for manual contact input/override if needed
        }

        if (showApiKeyDialog) {
            ApiKeyInputDialog(
                currentValue = currentApiKey, // This should be from viewModel ideally
                onDismiss = { showApiKeyDialog = false },
                onConfirm = {
                    viewModel.saveApiKey(it) // ViewModel should handle secure storage
                    currentApiKey = it // Update local state for display if not directly observing from VM
                    showApiKeyDialog = false
                }
            )
        }
    }
}

@Composable
fun HolidayPreferenceItem(holiday: Holiday, onHolidayToggled: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onHolidayToggled)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(holiday.name, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = holiday.isSelected, onCheckedChange = { onHolidayToggled() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyInputDialog(
    currentValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var apiKeyInput by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter ChatGPT API Key") },
        text = {
            OutlinedTextField(
                value = apiKeyInput,
                onValueChange = { apiKeyInput = it },
                label = { Text("API Key (e.g., sk-xxxx)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(apiKeyInput) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


