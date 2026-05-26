plugins {
    alias(libs.plugins.chat.android.library.compose)
}

android {
    namespace = "hu.tb.profile.presentation"
}

dependencies {
    implementation(projects.designSystem)

    implementation(libs.bundles.koin)
}