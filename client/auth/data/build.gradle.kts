plugins {
    alias(libs.plugins.chat.android.library)
    alias(libs.plugins.chat.koin)
    alias(libs.plugins.chat.ktor)
}

android {
    namespace = "hu.tb.auth.data"
}
dependencies {
    implementation(projects.auth.domain)
}
