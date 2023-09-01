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

rootProject.name = "FirstComposeApp"
include(":lab1")
include(":w1d5composelist")
include(":w2d4networkingandthreads")
include(":w2d5coroutine")
include(":w2d5couroutinesnet")
