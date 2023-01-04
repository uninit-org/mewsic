plugins {
    id("mewsic-library")
}
repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
}

val ktor_version: String by project

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation(project(":mewsic-commons"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktor_version")
            }
        }
        val browserMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktor_version")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-android:$ktor_version")
            }
        }
    }
}
