package com.example.aigiftrecommender.repository

import android.content.Context
import android.net.Uri
import com.example.aigiftrecommender.data.remote.ChatGPTApiService
import com.example.aigiftrecommender.data.remote.ChatCompletionRequest
import com.example.aigiftrecommender.data.remote.ChatMessage
import com.example.aigiftrecommender.model.Contact
import com.example.aigiftrecommender.model.GiftIdea
import com.example.aigiftrecommender.model.Holiday
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

interface GiftRepository {
    suspend fun getGiftRecommendations(
        contact: Contact,
        event: String, // e.g., "Birthday", or Holiday name
        apiKey: String // User-provided API Key
    ): Result<List<GiftIdea>>
}

@Singleton
class GiftRepositoryImpl @Inject constructor(
    private val chatGPTApiService: ChatGPTApiService,
    @ApplicationContext private val context: Context // For API key storage if needed later
) : GiftRepository {

    // Placeholder for secure API key retrieval. For now, passed as an argument.
    // In a real app, this would come from encrypted SharedPreferences or Keystore.
    // private suspend fun getApiKey(): String? {
    //    // Implement secure API key retrieval logic here
    //    return "YOUR_API_KEY" // Replace with actual retrieval
    // }

    override suspend fun getGiftRecommendations(
        contact: Contact,
        event: String,
        apiKey: String // API key is now passed directly
    ): Result<List<GiftIdea>> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || !apiKey.startsWith("Bearer sk-")) {
            return@withContext Result.failure(IllegalArgumentException("Invalid or missing API Key. Please set it in Settings."))
        }

        val prompt = constructPrompt(contact, event)
        val request = ChatCompletionRequest(
            messages = listOf(
                ChatMessage(role = "system", content = "You are a helpful assistant that suggests thoughtful gift ideas. Provide 3 diverse gift ideas based on the user's input. For each idea, provide a short name (max 5 words) and a brief, compelling description (1-2 sentences). Format each idea as: 'Name: [Gift Name]\nDescription: [Gift Description]'. Separate each gift idea with '---'. Do not include any other text or pleasantries."),
                ChatMessage(role = "user", content = prompt)
            )
        )

        try {
            val response = chatGPTApiService.getChatCompletions(apiKey = apiKey, requestBody = request)
            if (response.isSuccessful) {
                val chatResponse = response.body()
                if (chatResponse?.choices?.isNotEmpty() == true) {
                    val content = chatResponse.choices[0].message?.content ?: ""
                    val giftIdeas = parseGiftIdeas(content)
                    Result.success(giftIdeas)
                } else if (chatResponse?.error != null) {
                     Result.failure(Exception("API Error: ${chatResponse.error.message}"))
                } else {
                    Result.failure(Exception("No gift ideas found or empty response from API."))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("API request failed with code ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun constructPrompt(contact: Contact, event: String): String {
        val interests = contact.notes?.let { "Their notes mention: $it." } ?: "No specific notes available."
        val occasionInfo = if (event.equals("Birthday", ignoreCase = true) && contact.birthday != null) {
            "It's for their birthday on ${contact.birthday} (format may vary)."
        } else {
            "It's for the occasion: $event."
        }

        return "Suggest gift ideas for ${contact.name}. $occasionInfo $interests Consider their potential preferences and suggest unique and thoughtful gifts."
    }

    private fun parseGiftIdeas(responseText: String): List<GiftIdea> {
        val ideas = mutableListOf<GiftIdea>()
        val sections = responseText.split("---").map { it.trim() }.filter { it.isNotEmpty() }

        for (section in sections) {
            val nameMatch = "Name: (.*?)\\n".toRegex().find(section)
            val descMatch = "Description: (.*?)$".toRegex().find(section)

            val name = nameMatch?.groups?.get(1)?.value?.trim()
            val description = descMatch?.groups?.get(1)?.value?.trim()

            if (name != null && description != null) {
                val shoppingLink = generateAmazonLink(name)
                ideas.add(GiftIdea(name, description, shoppingLink))
            }
        }
        return ideas
    }

    private fun generateAmazonLink(giftName: String): String {
        // Basic Amazon search link generation
        return try {
            val encodedQuery = URLEncoder.encode(giftName, "UTF-8")
            "https://www.amazon.com/s?k=$encodedQuery"
        } catch (e: Exception) {
            // Fallback or error handling
            "https://www.amazon.com"
        }
    }
}

