package com.example.aigiftrecommender.model

data class Contact(
    val id: String,
    val name: String,
    val birthday: String?, // Format: YYYY-MM-DD or MM-DD
    val notes: String?
)

