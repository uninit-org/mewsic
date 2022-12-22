pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven("https://maven.martmists.com/releases/")
    }
}

includeBuild("gradle-build-plugin")

rootProject.name = "mewsic"

include(
    ":mewsic-commons",
    ":mewsic-mediaformat",
    ":mewsic-audioplayer",
    ":mewsic-innertube",
    ":mewsic-application",
)
