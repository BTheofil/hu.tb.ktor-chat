package hu.tb

import hu.tb.di.mainModule
import hu.tb.install.*
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import java.io.File

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
    installAuth()

    setupRouting()
}

private fun Application.connectDatabase() {
    val isDeveloperMode = environment.config.property("build.isDeveloperMode").getString().toBoolean()
    if (isDeveloperMode) {
        Database.connect(
            "jdbc:sqlite:build/test.db",
            "org.sqlite.JDBC"
        )
    } else {
        val dockerDbFile = File("app/database/app.db")
        val dbPath = if (dockerDbFile.parentFile?.exists() == true)
            dockerDbFile.absoluteFile
        else "build/data.db"

        Database.connect(
            url = "jdbc:sqlite:$dbPath",
            driver = "org.sqlite.JDBC"
        )
    }
}
