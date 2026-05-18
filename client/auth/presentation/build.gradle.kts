plugins {
    alias(libs.plugins.chat.android.library.compose)
    alias(libs.plugins.stability.analyzer)
}

android {
    namespace = "hu.tb.auth.presentation"
}

dependencies {
    implementation(projects.designSystem)
    implementation(projects.auth.data)
    implementation(projects.auth.domain)
    implementation(projects.datastore)

    implementation(libs.bundles.koin)
}