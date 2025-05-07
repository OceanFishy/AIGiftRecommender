package com.example.aigiftrecommender.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holidays")
data class Holiday(
    @PrimaryKey val name: String,
    val date: String, // Format: MM-DD, or YYYY-MM-DD for specific year holidays
    var isSelected: Boolean = true // User can select/deselect this holiday for recommendations
)

