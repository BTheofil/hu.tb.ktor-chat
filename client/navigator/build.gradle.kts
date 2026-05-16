plugins {
    alias(libs.plugins.chat.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "hu.tb.navigator"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(projects.auth.presentation)
    implementation(projects.dashboard)

    implementation(libs.bundles.nav3)
}