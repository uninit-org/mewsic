import org.mewsic.gradle.defaultProperty

plugins {
    id("mewsic-library")
}

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("io.sentry:sentry:6.10.0")
            }
        }
        val browserMain by getting {
            dependencies {
                implementation(npm("@sentry/browser", "6.10.0"))
                implementation(npm("@sentry/tracing", "6.10.0"))
            }
        }
    }
}

val sentry_dsn: String by defaultProperty(System.getenv("SENTRY_DSN") ?: "")

buildConfig {
    buildConfigField("String", "SENTRY_DSN", "\"$sentry_dsn\"")
}
