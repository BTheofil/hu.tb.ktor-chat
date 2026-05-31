plugins {
    alias(libs.plugins.chat.android.library.compose)
}

android {
    namespace = "hu.tb.message.presentation"
}

dependencies {
    implementation(projects.designSystem)
    implementation(projects.datastore)

    implementation(libs.bundles.koin)
}