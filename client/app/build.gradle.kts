plugins {
    alias(libs.plugins.chat.android.application)
    alias(libs.plugins.chat.android.application.compose)
    alias(libs.plugins.chat.koin)
}

android {
    namespace = "hu.tb.chat"

    defaultConfig {
        applicationId = "hu.tb.chat"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.auth.data)
    implementation(projects.auth.presentation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

}