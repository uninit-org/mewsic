plugins {
    `mewsic-library`
}

kotlin {
    sourceSets {
        appMain {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}")

                api("io.ktor:ktor-client-core:${Versions.ktor}")
                api("io.ktor:ktor-client-logging:${Versions.ktor}")
                api("io.ktor:ktor-client-content-negotiation:${Versions.ktor}")
                api("io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}")
            }
        }

        androidMain {
            dependencies {
                api("io.ktor:ktor-client-android:${Versions.ktor}")
            }
        }

        desktopMain {
            dependencies {
                api("io.ktor:ktor-client-cio:${Versions.ktor}")
            }
        }
    }
}
