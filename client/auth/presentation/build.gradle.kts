plugins {
    alias(libs.plugins.chat.android.library.compose)
    alias(libs.plugins.chat.koin)
}

android {
    namespace = "hu.tb.auth.presentation"
}

dependencies {
    implementation(projects.core.ui)
}