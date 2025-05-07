package com.example.aigiftrecommender.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatGPTApiService {
    @POST("v1/chat/completions") // Ensure this endpoint is correct for your API version
    suspend fun getChatCompletions(
        @Header("Authorization") apiKey: String, // e.g., "Bearer YOUR_API_KEY"
        @Body requestBody: ChatCompletionRequest
    ): Response<ChatCompletionResponse>
}

