package hu.tb

import hu.tb.di.mainModule
import hu.tb.install.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
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
