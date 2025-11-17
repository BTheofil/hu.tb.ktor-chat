import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlExtension

plugins {
    application
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    id("com.gradleup.shadow") version "8.3.9"
    id("com.google.cloud.tools.appengine") version "2.8.0"
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
    implementation(ktorLibs.server.statusPages)
    implementation(ktorLibs.server.sessions)
    implementation(ktorLibs.server.websockets)
    implementation(ktorLibs.websockets.serialization)

    implementation("ch.qos.logback:logback-classic:1.5.20")

    val koinVersion = "4.1.1"
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    val sqlVersion = "1.0.0-rc-3"
    implementation("org.jetbrains.exposed:exposed-core:$sqlVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$sqlVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$sqlVersion")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")
}

appengine {
    configure<AppEngineAppYamlExtension> {
        stage {
            setArtifact("build/libs/${project.name}-${version}-all.jar")
        }
        deploy {
            version = "GCLOUD_CONFIG"
            projectId = "GCLOUD_CONFIG"
        }
    }
}
