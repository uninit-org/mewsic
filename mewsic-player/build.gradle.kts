plugins {
    `mewsic-library`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":mewsic-media"))
                api(project(":mewsic-utils"))
            }
        }
    }
}
