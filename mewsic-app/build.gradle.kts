import dev.icerock.gradle.MRVisibility

plugins {
    `mewsic-app`
    id("org.jetbrains.compose")
    id("dev.icerock.mobile.multiplatform-resources")
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
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:${Versions.decompose}")
                implementation("dev.icerock.moko:resources:${Versions.mokoResources}")
                implementation("dev.icerock.moko:resources-compose:${Versions.mokoResources}")
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

multiplatformResources {
    multiplatformResourcesPackage = "${android.namespace}.resources"
    multiplatformResourcesVisibility = MRVisibility.Internal
    multiplatformResourcesSourceSet = "appMain"
}

compose {
    desktop {
        application {
            mainClass = "dev.uninit.mewsic.app.MainKt"

            nativeDistributions {
                packageName = "Mewsic"
                description = "Cross-platform Music Player"
                packageVersion = (project.version as String).substringAfter('v').substringBefore('-')
                copyright = "© 2022 Mewsic Developers. All rights reserved."
                modules("java.instrument", "jdk.unsupported")

                windows {
                    perUserInstall = true
                    dirChooser = true
                }

                linux {

                }
            }

            buildTypes {
                release {
                    proguard {
                        configurationFiles.from(
                            project.rootProject.file("proguard/desktop-rules.pro")
                        )
                    }
                }
            }
        }
    }
}
