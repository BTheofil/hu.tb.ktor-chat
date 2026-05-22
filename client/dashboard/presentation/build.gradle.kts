plugins {
    alias(libs.plugins.chat.android.library.compose)
    alias(libs.plugins.stability.analyzer)
}

android {
    namespace = "hu.tb.dashboard.presentation"
}

dependencies {
    implementation(projects.network)
    implementation(projects.designSystem)
    implementation(projects.datastore)

    implementation(libs.bundles.koin)
}