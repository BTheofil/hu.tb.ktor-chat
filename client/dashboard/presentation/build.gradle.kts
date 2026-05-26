plugins {
    alias(libs.plugins.chat.android.library.compose)
}

android {
    namespace = "hu.tb.dashboard.presentation"
}

dependencies {
    implementation(projects.network)
    implementation(projects.designSystem)
    implementation(projects.datastore)
    implementation(projects.dashboard.domain)

    implementation(libs.bundles.koin)
}