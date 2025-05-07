package com.example.aigiftrecommender.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiftrecommender.model.Holiday
import com.example.aigiftrecommender.repository.HolidayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val holidayRepository: HolidayRepository
) : ViewModel() {

    val holidays: StateFlow<List<Holiday>> = holidayRepository.getAllHolidays()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // In a real app, API key would be stored securely (e.g., EncryptedSharedPreferences)
    // For this example, we simulate it. The actual input and storage would be in SettingsScreen.
    private var _apiKey: String = "" // Placeholder
    val apiKey: String
        get() = _apiKey

    init {
        viewModelScope.launch {
            holidayRepository.prePopulateHolidaysIfNeeded()
            // Load API key from secure storage here
        }
    }

    fun updateHolidaySelection(holiday: Holiday, isSelected: Boolean) {
        viewModelScope.launch {
            holidayRepository.updateHoliday(holiday.copy(isSelected = isSelected))
        }
    }

    fun saveApiKey(key: String) {
        // In a real app, save this securely.
        _apiKey = key
        // Persist the key to EncryptedSharedPreferences or similar
    }

    fun loadApiKey() {
        // Load API key from secure storage
        // For now, this is a placeholder
        // _apiKey = secureStorage.getApiKey() ?: ""
    }

    // Add functions for other settings as needed
}

