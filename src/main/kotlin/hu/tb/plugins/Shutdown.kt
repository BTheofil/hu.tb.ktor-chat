package hu.tb.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.ShutDownUrl

fun Application.installShutdown() {
    install(ShutDownUrl.ApplicationCallPlugin) {
        shutDownUrl = "/shutdown"
        exitCodeSupplier = { 0 }
    }
}