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
include(":w3d5retrofit")
include(":w3d1room")
include(":w3d5beacon")
include(":w4d5bluetooth")
include(":w4d5graph")
