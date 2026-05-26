plugins {
    alias(libs.plugins.chat.android.application)
    alias(libs.plugins.chat.android.application.compose)
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
    implementation(projects.auth.domain)
    implementation(projects.auth.presentation)
    implementation(projects.dashboard.presentation)
    implementation(projects.profile.presentation)
    implementation(projects.datastore)
    implementation(projects.designSystem)
    implementation(projects.navigator)
    implementation(projects.network)

    implementation(libs.bundles.koin)
    implementation(libs.androidx.core.splashscreen)
}