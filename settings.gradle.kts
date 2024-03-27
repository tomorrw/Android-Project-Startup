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
    versionCatalogs {
        create("libraries") {
            from(files("gradle/dependencies.versions.toml"))
        }
    }
}

rootProject.name = "projectStartup"
include(":app")
include(":AppUpdate")
include(":InternetConnectivity")
include(":Navigation")
include(":ReadViewModel")
include(":RequestPermission")
