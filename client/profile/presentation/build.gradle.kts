plugins {
    alias(libs.plugins.chat.android.library.compose)
}

android {
    namespace = "hu.tb.profile.presentation"
}

dependencies {
    implementation(projects.network)
    implementation(projects.designSystem)
    implementation(projects.profile.domain)
    implementation(projects.datastore)

    implementation(libs.bundles.koin)
}