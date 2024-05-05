import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    `kotlin-dsl`
    id("com.github.ben-manes.versions") version "0.51.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.9.23"))
    implementation(kotlin("serialization", "1.9.23"))

    implementation("com.github.ben-manes:gradle-versions-plugin:0.51.0")
    implementation("com.github.gmazzo.buildconfig:plugin:5.3.5")

    implementation("org.jetbrains.compose:compose-gradle-plugin:1.6.10-beta03")
    implementation("com.android.tools.build:gradle:8.4.0")
    implementation("org.bytedeco.gradle-javacpp-platform:org.bytedeco.gradle-javacpp-platform.gradle.plugin:1.5.10")
}

tasks {
    withType<DependencyUpdatesTask> {
        val unstableKeywords = listOf("alpha", "beta", "rc", "cr", "m", "preview", "dev")
        rejectVersionIf {
            unstableKeywords.any { keyword -> candidate.version.contains(keyword, ignoreCase = true) } && !unstableKeywords.any { keyword -> currentVersion.contains(keyword, ignoreCase = true) }
        }
    }
}
