package org.mewsic.gradle.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.*
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.*
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.util.*

open class MewsicApplicationPlugin : MewsicCommonPlugin {
    override fun apply(target: Project) = target.apply()

    @JvmName("apply0")
    private fun Project.apply() {
        applyCommon()
        applicationTasks()
        setupCompose()

        repositories {
            maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        }
    }

    override fun Project.setupAndroid() {
        apply<AppPlugin>()

        configure<BaseAppModuleExtension> {
            compileSdk = 32

            defaultConfig {
                minSdk = 26
                targetSdk = 32
                versionName = project.version as String
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            buildTypes {
                debug {
                    versionNameSuffix = "-debug"
                    proguardFiles(getDefaultProguardFile("proguard-defaults.txt"), rootDir.resolve("proguard/android-rules.pro"))
                }
                release {
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), rootDir.resolve("proguard/android-rules.pro"))
                }
            }

            sourceSets {
                named("main") {
                    manifest.srcFile("src/androidMain/AndroidManifest.xml")
                    res.srcDirs("src/androidMain/resources")
                }
            }
        }
    }

    private fun Project.setupCompose() {
        apply<ComposePlugin>()

        configure<ComposeExtension> {
            configure<DesktopExtension> {
                application {
                    nativeDistributions {
                        version = (project.version as String).substringAfter('v').substringBefore('-')
                        licenseFile.set(rootDir.resolve("LICENSE"))

                        outputBaseDir.set(rootProject.buildDir.resolve("dist"))

                        targetFormats(
                            // MacOS
                            TargetFormat.Dmg,
                            // Windows
                            TargetFormat.Msi,
                            // Linux
                            TargetFormat.Deb,
                        )
                    }

                    buildTypes.release.proguard {
                        configurationFiles.from(rootDir.resolve("proguard/desktop-rules.pro"))
                    }
                }
            }
        }

        configure<KotlinMultiplatformExtension> {
            val compose = (this as ExtensionAware).the<ComposePlugin.Dependencies>()

            sourceSets {
                val commonMain by getting {
                    dependencies {
                        implementation(compose.ui)
                        implementation(compose.foundation)
                        implementation(compose.material)
                        implementation(compose.runtime)
                    }
                }

                val browserMain by getting {
                    dependencies {
                        implementation(compose.web.core)
                        implementation(compose.web.svg)
                    }
                }

                val desktopMain by getting {
                    dependencies {
                        implementation(compose.desktop.currentOs)
                    }
                }

                val androidMain by getting {
                    dependencies {
                        implementation("androidx.appcompat:appcompat:1.5.1")
                        implementation("androidx.activity:activity-compose:1.5.0")
                    }
                }
            }
        }
    }

    private fun Project.applicationTasks() {
        tasks {
            named("build") {
                if (isDevelopment()) {
                    dependsOn(
                        // Android
                        "assembleDebug",
                        // Desktop
                        "packageDistributionForCurrentOS",
                        // Web is handled by ac-backend
                    )
                } else {
                    dependsOn(
                        // Android
                        "assembleRelease",
                        // Desktop
                        "packageReleaseDistributionForCurrentOS",
                        // Web is handled by ac-backend
                    )
                }
            }
        }
    }
}
