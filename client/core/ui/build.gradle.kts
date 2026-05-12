plugins {
    alias(libs.plugins.chat.android.library.compose)
}

android {
    namespace = "hu.tb.ui"
}

dependencies {
    implementation(libs.androidx.compose.ui.text.google.fonts)
}