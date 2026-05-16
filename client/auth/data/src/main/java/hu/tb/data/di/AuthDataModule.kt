package hu.tb.data.di

import hu.tb.data.LoginRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authDataModule = module {
    single<HttpClient> {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json { prettyPrint = true })
            }
            defaultRequest {
                url("http://[2a01:4f9:c014:f7e9::1]:8080")
            }
        }
    }
    singleOf(::LoginRepository)
}