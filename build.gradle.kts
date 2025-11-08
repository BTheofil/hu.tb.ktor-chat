plugins {
    application
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
}

group = "hu.tb"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.sessions)
    implementation(ktorLibs.server.websockets)
    implementation(ktorLibs.websockets.serialization)

    implementation("ch.qos.logback:logback-classic:1.5.20")

    val mongoVersion = "5.6.1"
    implementation("org.mongodb:bson-kotlinx:$mongoVersion")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongoVersion")

    implementation("io.insert-koin:koin-ktor:4.1.1")
    implementation("io.insert-koin:koin-logger-slf4j:4.1.1")
}
