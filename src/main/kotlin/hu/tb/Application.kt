package hu.tb

import hu.tb.di.mainModule
import hu.tb.install.*
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    connectDatabase()

    install(Koin) {
        slf4jLogger()
        modules(mainModule)
    }

    installContentNegotiation()
    installStatusPage()
    installWebSockets()
    installShutdown()

    setupRouting()
    //configureSecurity()
}

private fun connectDatabase() =
    Database.connect(
        url = "jdbc:sqlite:data.db",
        driver = "org.sqlite.JDBC"
    )