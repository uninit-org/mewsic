plugins {
    `mewsic-library`
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
            }
        }

        desktopMain {
            dependencies {
                implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
                implementation("ch.qos.logback:logback-classic:${Versions.logback}")
            }
        }
    }
}
