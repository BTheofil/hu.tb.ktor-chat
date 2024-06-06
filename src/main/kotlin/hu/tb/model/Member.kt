package hu.tb.model

import io.ktor.websocket.*

data class Member(
    val name: String,
    val socket: WebSocketSession
)