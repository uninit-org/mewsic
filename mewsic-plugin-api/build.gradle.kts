plugins {
    `mewsic-library`
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mewsic-api-clients:mewsic-client-soundcloud"))
                implementation(project(":mewsic-engine"))
                implementation(project(":mewsic-media"))
                implementation(project(":mewsic-player"))
                implementation(project(":mewsic-utils"))
            }
        }

        androidMain {
            dependencies {
                implementation("androidx.activity:activity-compose:${Versions.activityCompose}")
            }
        }

        appMain {
            dependencies {
                implementation(compose.ui)
                implementation(compose.animation)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(compose.uiTooling) {
                    exclude("org.jetbrains.compose.material", "material")
                    exclude("androidx.compose.material", "material")
                }
                implementation(compose.components.resources)
                implementation("com.russhwolf:multiplatform-settings:${Versions.multiplatformSettings}")
                implementation("com.arkivanov.decompose:decompose:${Versions.decompose}")
                implementation("com.arkivanov.decompose:extensions-compose:${Versions.decompose}")

                implementation("com.google.auto.service:auto-service-annotations:${Versions.autoService}")
            }
        }

        desktopMain {
            dependencies {
                implementation(compose.desktop.currentOs) {
                    exclude("org.jetbrains.compose.material", "material")
                }
            }
        }
    }
}
