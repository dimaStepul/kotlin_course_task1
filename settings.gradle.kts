
rootProject.name = "test-2"

include("task1")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm") version "1.8.20"
        kotlin("plugin.serialization") version "1.8.20"
    }
}
