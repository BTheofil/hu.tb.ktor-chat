package hu.tb

import com.mongodb.kotlin.client.coroutine.MongoClient
import hu.tb.di.mainModule
import hu.tb.plugins.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(
            org.koin.dsl.module {
                single {
                    MongoClient.create(
                        environment.config.propertyOrNull("ktor.mongo.uri")?.getString()
                            ?: throw RuntimeException("Failed to access MongoDB URI.")
                    ).getDatabase(environment.config.property("ktor.mongo.database").getString())
                }
            }, mainModule
        )
    }

    configureSockets()
    configureRouting()
    configureSerialization()
    configureSecurity()
}
