plugins {
    `kotlin-dsl`
}

group = "hu.tb.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    //compileOnly(libs.ksp.gradlePlugin)
}


gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "hutb.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "hutb.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
    }
}