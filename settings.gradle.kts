pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AvivPropertyApp"
include(":app")
include(":core")
include(":data")
include(":domain")
include(":feature")
include(":core:common")
include(":core:network")
include(":core:designsystem")
include(":core:ui")
include(":core:model")
include(":feature:listing")
include(":feature:detail")
