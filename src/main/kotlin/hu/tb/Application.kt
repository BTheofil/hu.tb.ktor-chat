package hu.tb

import com.mongodb.kotlin.client.coroutine.MongoClient
import hu.tb.di.mainModule
import hu.tb.install.installWebSockets
import hu.tb.install.installContentNegotiation
import hu.tb.install.installShutdown
import hu.tb.install.installStatusPage
import hu.tb.install.setupRouting
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
                    val login = System.getenv("MONGO_SERVER_LOGIN")
                    val pw = System.getenv("MONGO_SERVER_PW")
                    MongoClient.create(
                        "mongodb+srv://$login:$pw@clusterforchat.3tnpmhn.mongodb.net/?appName=ClusterForChat"
                    )
                }
            }, mainModule
        )
    }

    installContentNegotiation()
    installStatusPage()
    installWebSockets()
    installShutdown()

    setupRouting()
    //configureSecurity()
}
