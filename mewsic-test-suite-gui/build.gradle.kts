plugins {
    id("mewsic-application")
}

compose.desktop {
    application {
        mainClass = "org.mewsic.testsuite.MainAppKt"

        nativeDistributions {
            packageName = "mewsic GUI Test Suite"
            description = "Test Suite for mewsic libraries and GUI components"
            packageVersion = (project.version as String).substringAfter('v').substringBefore('-')
            copyright = "© 2022 Mewsic Developers. All rights reserved."
            modules("java.instrument", "jdk.unsupported")

            windows {
                perUserInstall = true
                dirChooser = true
            }
            linux {

            }
            macOS {
                bundleID = group as String
            }
        }
    }
}

val ktor_version: String by project


kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":mewsic-jaad"))
                implementation(project(":mewsic-audioplayer"))
                implementation(project(":mewsic-commons"))
                implementation("io.ktor:ktor-client-core:$ktor_version")
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
