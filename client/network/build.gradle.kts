plugins {
    alias(libs.plugins.chat.android.library)
    kotlin("plugin.serialization") version "2.4.0"
}

android {
    namespace = "hu.tb.network"
}

dependencies {
    implementation(projects.auth.domain)
    implementation(projects.dashboard.domain)
    implementation(projects.profile.domain)
    implementation(projects.message.domain)
    implementation(projects.datastore)

    implementation(libs.bundles.koin)
    implementation(libs.bundles.ktor)
}
