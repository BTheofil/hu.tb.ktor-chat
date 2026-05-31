pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "Chat"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":auth:presentation")
include(":auth:domain")
include(":design-system")
include(":datastore")
include(":navigator")
include(":network")
include(":dashboard:domain")
include(":dashboard:presentation")
include(":profile:presentation")
include(":profile:domain")
include(":message:presentation")
