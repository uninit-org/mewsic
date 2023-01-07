plugins {
    id("mewsic-library")
}
repositories {
}

val ktor_version: String by project

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":mewsic-commons"))
            }
        }
        val jvmMain by getting {
            dependencies {
            }
        }
        val browserMain by getting {
            dependencies {
            }
        }
        val androidMain by getting {
            dependencies {
            }
        }
    }
}
