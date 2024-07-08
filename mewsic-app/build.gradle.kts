plugins {
    `mewsic-app`
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mewsic-api-clients:mewsic-client-soundcloud"))
                implementation(project(":mewsic-engine"))
                implementation(project(":mewsic-media"))
                implementation(project(":mewsic-player"))
                implementation(project(":mewsic-plugin-api"))
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

dependencies {
    add("kspAndroid", "dev.zacsweers.autoservice:auto-service-ksp:${Versions.autoServiceKsp}")
    add("kspDesktop", "dev.zacsweers.autoservice:auto-service-ksp:${Versions.autoServiceKsp}")
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
                    iconFile = project.file("src/androidMain/res/mipmap-xxxhdpi/ic_launcher.webp")
                }

                linux {
                    iconFile = project.file("src/androidMain/res/mipmap-xxxhdpi/ic_launcher.webp")
                }
            }

            buildTypes {
                release {
                    proguard {
                        configurationFiles.from(
                            project.rootProject.file("proguard/common-rules.pro"),
                            project.rootProject.file("proguard/desktop-rules.pro"),
                        )
                    }
                }
            }
        }
    }
}
