import buildutils.configureDetekt
import buildutils.createDetektTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.research.code.submissions.clustering.buildutils.configureDiktat
import org.jetbrains.research.code.submissions.clustering.buildutils.createDiktatTask

plugins {
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig") version "3.0.3"
    kotlin("plugin.serialization")
}

group = "org.example"
version = "1.0-SNAPSHOT"

allprojects {
    apply {
        plugin("java")
        plugin("kotlin")
        plugin("kotlinx-serialization")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        val kotlinVersion = "1.7.20"
        implementation(kotlin("gradle-plugin", version = kotlinVersion))
        implementation(kotlin("serialization", version = kotlinVersion))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")

        implementation("com.github.ajalt.clikt:clikt:4.0.0")

        implementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
        runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
        implementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
        runtimeOnly("org.junit.platform:junit-platform-console:1.9.0")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    configureDiktat()
    configureDetekt()
}

createDiktatTask()
createDetektTask()
