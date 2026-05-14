plugins {
    alias(libs.plugins.chat.android.library.compose)
}

android {
    namespace = "hu.tb.design_system"
}

dependencies {
    implementation(libs.androidx.compose.ui.text.google.fonts)
}