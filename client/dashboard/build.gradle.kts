plugins {
    alias(libs.plugins.chat.android.library.compose)
    alias(libs.plugins.stability.analyzer)
}

android {
    namespace = "hu.tb.dashboard"
}

dependencies {
    implementation(projects.designSystem)

    implementation(libs.bundles.koin)
}