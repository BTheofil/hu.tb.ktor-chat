package hu.tb.model

import io.ktor.websocket.*

data class Member(
    val name: String,
    val activeSocketSession: WebSocketSession,
    val friendsList: List<Member> = emptyList()
)