package hu.tb.install

import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

fun Application.installWebSockets() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        // Without a ping a client that vanishes, for example on a lost mobile connection, leaves a
        // half open session in groupConnections forever, and the client never learns it is offline.
        pingPeriod = 15.seconds
        timeout = 30.seconds
    }
}
