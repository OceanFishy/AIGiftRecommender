package com.example.aigiftrecommender.data.remote

// Request body for ChatGPT API (simplified example)
// Refer to official OpenAI documentation for the exact structure needed for your model (e.g., gpt-3.5-turbo)
data class ChatCompletionRequest(
    val model: String = "gpt-3.5-turbo", // Or your preferred model
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7 // Controls randomness, adjust as needed
)

data class ChatMessage(
    val role: String, // "system", "user", or "assistant"
    val content: String
)

// Response body from ChatGPT API (simplified example)
data class ChatCompletionResponse(
    val id: String?,
    val `object`: String?,
    val created: Long?,
    val model: String?,
    val choices: List<Choice>?,
    val usage: Usage?,
    val error: ApiError? // For handling API errors
)

data class Choice(
    val index: Int?,
    val message: ChatMessage?,
    val finish_reason: String?
)

data class Usage(
    val prompt_tokens: Int?,
    val completion_tokens: Int?,
    val total_tokens: Int?
)

data class ApiError(
    val message: String,
    val type: String?,
    val param: String?,
    val code: String?
)

