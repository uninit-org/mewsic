plugins {
    `mewsic-library`
}

val javacpp by configurations.creating

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":mewsic-api-clients:mewsic-client-soundcloud"))
                implementation(project(":mewsic-utils"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
            }
        }

        appMain {
            dependencies {
                api("org.bytedeco:ffmpeg:${Versions.ffmpeg}")
            }
        }

        desktopMain {
            dependencies {
                // Detect OS
                when (val platform = System.getProperty("os.name").lowercase()) {
                    "linux" -> {
                        implementation("org.bytedeco:ffmpeg:${Versions.ffmpeg}:linux-x86_64")
                    }
                    "windows" -> {
                        implementation("org.bytedeco:ffmpeg:${Versions.ffmpeg}:windows-x86_64")
                    }
                    else -> {
                        throw IllegalStateException("Unsupported OS: $platform!")
                    }
                }
            }
        }
    }
}

dependencies {
    javacpp("org.bytedeco:ffmpeg:${Versions.ffmpeg}:android-arm")
    javacpp("org.bytedeco:ffmpeg:${Versions.ffmpeg}:android-arm64")
    javacpp("org.bytedeco:ffmpeg:${Versions.ffmpeg}:android-x86")
    javacpp("org.bytedeco:ffmpeg:${Versions.ffmpeg}:android-x86_64")
}

android {
    sourceSets["main"].jniLibs.srcDirs(layout.buildDirectory.dir("javacpp/natives/lib"))
}

tasks {
    val extractJavacppNatives by registering(Copy::class) {
        exclude(
            "ffmpeg",
            "ffprobe",
            "libavdevice.so",
            "libjniavdevice.so",
            "libswresample.so",
            "libjniswresample.so",
            "libswscale.so",
            "libjniswscale.so",
        )

        from(javacpp.map { zipTree(it) }) {
            include(
                "lib/**/libavcodec.so",
                "lib/**/libjniavcodec.so",

                "lib/**/libavformat.so",
                "lib/**/libjniavformat.so",

                "lib/**/libavfilter.so",
                "lib/**/libjniavfilter.so",

                "lib/**/libavutil.so",
                "lib/**/libjniavutil.so",

                "lib/**/libswresample.so",
                "lib/**/libjniswresample.so",

                "lib/**/libswscale.so",
                "lib/**/libjniswscale.so",
            )
        }
        into(layout.buildDirectory.dir("javacpp/natives"))
    }

    named("preBuild") {
        dependsOn(extractJavacppNatives)
    }
}
