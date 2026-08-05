package hu.tb.message.domain

enum class ConnectionStatus(
    val label: String
) {
    CONNECTING(
        label = "Connecting..."
    ),
    CONNECTED(
        label = "Connected"
    ),
    DISCONNECTED(
        label = "Connection lost"
    )
}