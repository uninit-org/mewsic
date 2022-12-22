plugins {
    id("mewsic-library")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":mewsic-commons"))
            }
        }
    }
}
