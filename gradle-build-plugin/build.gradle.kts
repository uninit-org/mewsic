plugins {
    kotlin("jvm") version "1.7.20"
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
    maven("https://maven.martmists.com/releases/")
}

buildDir = rootDir.resolve("../build/${name}")

dependencies {
    implementation(gradleKotlinDsl())
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("serialization"))
    implementation("com.martmists.commons:commons-gradle:1.0.4")

    // Plugins we configure
    implementation("com.github.ben-manes:gradle-versions-plugin:0.44.0")
    implementation("com.github.gmazzo:gradle-buildconfig-plugin:3.0.3")  // Do not update: Not compatible with Kotlin Generator

    implementation("com.android.tools.build:gradle:7.3.1")

    implementation("org.jetbrains.compose:compose-gradle-plugin:1.3.0-rc01")
}

gradlePlugin {
    plugins {
        create("mewsic-root") {
            id = "mewsic-root"
            implementationClass = "org.mewsic.gradle.plugin.MewsicRootPlugin"
        }
        create("mewsic-library") {
            id = "mewsic-library"
            implementationClass = "org.mewsic.gradle.plugin.MewsicLibraryPlugin"
        }
        create("mewsic-application") {
            id = "mewsic-application"
            implementationClass = "org.mewsic.gradle.plugin.MewsicApplicationPlugin"
        }
    }
}
