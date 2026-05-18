plugins {
    alias(libs.plugins.chat.android.library)
    alias(libs.plugins.kotlinx.serialization.json)
}

android {
    namespace = "hu.tb.datastore"
}

dependencies {
    implementation(libs.bundles.datastore)
    implementation(libs.bundles.koin)
}