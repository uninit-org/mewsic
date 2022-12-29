plugins {
    id("mewsic-library")
}


kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":mewsic-commons"))
            }
        }
    }
}
