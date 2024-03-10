plugins {
    `mewsic-library`
}

kotlin {
    sourceSets {
        appMain {
            dependencies {
                api(project(":mewsic-api-clients:mewsic-client-common"))
                api("org.jetbrains.kotlinx:kotlinx-datetime:${Versions.datetime}")
            }
        }
    }
}
