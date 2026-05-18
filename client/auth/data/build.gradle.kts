plugins {
    alias(libs.plugins.chat.android.library)
    kotlin("plugin.serialization") version "2.3.21"
}

android {
    namespace = "hu.tb.auth.data"
}
dependencies {
    implementation(projects.auth.domain)

    implementation(libs.bundles.koin)
    implementation(libs.bundles.ktor)
}
