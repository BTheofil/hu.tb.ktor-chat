package hu.tb.network.di

import hu.tb.network.dashboard.DashboardRepository
import hu.tb.network.login.LoginRepository
import hu.tb.network.profile.ProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    explicitNulls = false
                    prettyPrint = true
                })
            }
            defaultRequest {
                url("http://[2a01:4f9:c014:f7e9::1]:8080")
            }
        }
    }
    singleOf(::LoginRepository)
    singleOf(::DashboardRepository)
    singleOf(::ProfileRepository)
}