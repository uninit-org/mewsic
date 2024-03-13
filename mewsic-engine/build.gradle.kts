import com.android.build.gradle.tasks.ExternalNativeBuildTask
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    `mewsic-library`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mewsic-utils"))
            }
        }

        desktopMain {
            dependencies {

                // TODO: Only on Linux
                implementation("com.github.hypfvieh:dbus-java-osgi:${Versions.dbus}")
                implementation("com.github.hypfvieh:dbus-java-transport-junixsocket:${Versions.dbus}")
            }
        }
    }

    linuxTargets {
        binaries {
            sharedLib {

            }
        }

        compilations["main"].cinterops.register("gstreamer") {
            defFile = projectDir.resolve("src/linuxNativeMain/cinterops/gstreamer.def")
        }
        compilations["main"].cinterops.register("gio") {
            defFile = projectDir.resolve("src/linuxNativeMain/cinterops/gio.def")
        }
    }

    androidNativeTargets {
        binaries {
            staticLib {

            }
        }

        compilations["main"].cinterops.register("audio_api") {
            defFile = projectDir.resolve("src/androidNativeMain/cinterops/audio_api.def")
            includeDirs(projectDir.resolve("src/androidNativeMain/cinterops/include/"))
        }
    }

    nativeTargets {
        compilations.configureEach {
            compilerOptions.configure {
                freeCompilerArgs.addAll(
                    "-opt-in=kotlin.experimental.ExperimentalNativeApi",
                    "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
                    "-opt-in=kotlinx.cinterop.UnsafeNumber",
                )
            }
        }

        val familyName = konanTarget.family.name
        val architectureName = konanTarget.architecture.name

        val isForCurrentOs = when(val os = HostManager.hostOs()) {
            "linux" -> familyName == "LINUX"
            else -> TODO(os)
        }
        val isForCurrentArch = when(val arch = HostManager.hostArch()) {
            "x86_64" -> architectureName == "X64"
            else -> TODO(arch)
        }

        if (isForCurrentOs && isForCurrentArch) {
            tasks.named<Copy>("desktopProcessResources") {
                val linkTask = tasks.getByName("linkDebugShared${this@nativeTargets.name.capitalized()}")

                dependsOn(linkTask)
                from(linkTask)
            }
        }
    }
}

android {
    defaultConfig {
        externalNativeBuild {
            cmake {
                arguments += listOf("-DNATIVES_BUILD_DIR=${layout.buildDirectory.dir("bin").get().asFile.absolutePath}")
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = projectDir.resolve("src/androidMain/cpp/CMakeLists.txt")
        }
    }
}

tasks {
    afterEvaluate {
        withType(ExternalNativeBuildTask::class) {
            if ("[" in name) {
                val nativeTarget = when (val type = name.substringAfter('[').substringBefore(']')) {
                    "armeabi-v7a" -> "Arm32"
                    "arm64-v8a" -> "Arm64"
                    "x86" -> "X86"
                    "x86_64" -> "X64"
                    else -> error("Unknown type: $type")
                }
                val variant = variantName.capitalized()
                dependsOn("link${variant}StaticAndroidNative${nativeTarget}")
            }
        }
    }
}
