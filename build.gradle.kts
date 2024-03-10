import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("com.github.ben-manes.versions")
}

group = "dev.uninit"
version = "1.0.0"

allprojects {
    repositories {
        mavenCentral()
        google()
    }

    tasks {
        withType<DependencyUpdatesTask> {
            val unstableKeywords = listOf("alpha", "beta", "rc", "cr", "m", "preview", "dev")
            rejectVersionIf {
                unstableKeywords.any { keyword -> candidate.version.contains(keyword, ignoreCase = true) } && !unstableKeywords.any { keyword -> currentVersion.contains(keyword, ignoreCase = true) }
            }
        }
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version
    layout.buildDirectory = rootProject.layout.buildDirectory.dir(name)
}
