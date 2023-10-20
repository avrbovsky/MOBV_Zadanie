pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = "sk.eyJ1IjoieHZyYm92c2t5YSIsImEiOiJjbG4ydmV0NmwwZGthMnFtdTE3NHFmZTQwIn0.jAGGv5RyM0S8WUjTDS2auA"
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

rootProject.name = "MOBV Zadanie"
include(":app")
 