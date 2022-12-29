plugins {
    id("mewsic-application")
}

compose.desktop {
    application {
        mainClass = "org.mewsic.application.MainKt"

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
            macOS {
                bundleID = group as String
            }
        }
    }
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":mewsic-jaad"))
            }
        }
    }
}
