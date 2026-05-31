package hu.tb.network.message

import io.ktor.client.HttpClient
import io.ktor.client.request.get

class MessageRepository(
    private val client: HttpClient
) {
    suspend fun sendMessage() {
        client.get("/groupConnect")
    }
}