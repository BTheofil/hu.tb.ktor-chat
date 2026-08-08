package hu.tb.network.di

import hu.tb.network.auth.AuthRepository
import hu.tb.network.dashboard.DashboardRepository
import hu.tb.network.message.MessageRepository
import hu.tb.network.profile.ProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

val networkModule = module {
    single<HttpClient> {
        HttpClient(OkHttp) {
            engine {
                config {
                    pingInterval(15.seconds)
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    prettyPrint = true
                })
            }
            install(Auth) {}
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
            defaultRequest {
                url("https://theohome-chat.duckdns.org")
            }
        }
    }
    singleOf(::AuthRepository)
    singleOf(::DashboardRepository)
    singleOf(::ProfileRepository)
    singleOf(::MessageRepository)
}