plugins {
    `mewsic-app`
    id("org.jetbrains.compose")
}

kotlin {
    sourceSets {
        commonMain {
//            kotlin.setSrcDirs(kotlin.srcDirs.filter { !it.absolutePath.contains("compose/resourceGenerator") })

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
//            configureResClassGeneration()
//            configureResourceAccessorsGeneration()

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

compose {
    resources {
        packageOfResClass = "dev.uninit.mewsic.app.generated"
        generateResClass = always
    }

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
