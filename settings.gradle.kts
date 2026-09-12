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

// This is only the internal Gradle build identifier (never shown to users).
// The app's real display name "الطقس" is set via app_name in strings.xml.
rootProject.name = "al-taqs-weather"
include(":app")
