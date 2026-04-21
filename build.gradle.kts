plugins {
    application
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.serialization") version "2.3.20"
    id("io.ktor.plugin") version "3.4.2"
    id("com.google.cloud.tools.jib") version "3.5.3"
}

group = "hu.tb"
version = "0.1.0"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

jib {
    to {
        image = "btheofil/ktor_server"
        tags = setOf(version.toString())
    }
}

dependencies {
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.statusPages)
    implementation(ktorLibs.server.auth)
    implementation(ktorLibs.server.auth.jwt)
    implementation(ktorLibs.server.websockets)
    implementation(ktorLibs.websockets.serialization)
    implementation(ktorLibs.server.callLogging)
    implementation(otherLibs.logger.classic)
    implementation(otherLibs.bundles.koin)
    implementation(otherLibs.bundles.exposed)
    implementation(otherLibs.sqlite)

    testImplementation(ktorLibs.server.testHost)
    testImplementation(ktorLibs.client.contentNegotiation)
    testImplementation(otherLibs.kotlin.test)
}