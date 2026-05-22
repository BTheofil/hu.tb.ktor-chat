plugins {
    alias(libs.plugins.chat.android.library)
    kotlin("plugin.serialization") version "2.3.21"
}

android {
    namespace = "hu.tb.network"
}

dependencies {
    implementation(projects.auth.domain)
    implementation(projects.dashboard.domain)

    implementation(libs.bundles.koin)
    implementation(libs.bundles.ktor)
}
