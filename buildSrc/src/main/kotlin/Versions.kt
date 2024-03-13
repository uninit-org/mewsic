import org.gradle.api.JavaVersion
import org.jetbrains.compose.ComposeBuildConfig

object Versions {
    // Kotlin core
    val kotlin = "1.9.22"
    val coroutines = "1.8.0"
    val serialization = "1.6.3"
    val datetime = "0.5.0"

    // Libraries
    val ktor = "2.3.6"

    // Compose
    val compose = ComposeBuildConfig.composeVersion
    val decompose = "2.2.2"
    val orbital = "0.3.2"
    val multiplatformSettings = "1.1.1"
    val activityCompose = "1.8.2"  // Android-only
    val mokoResources = "0.23.0"  // Might be deprecated with Compose 1.6.0?

    // Javacpp
    val javacpp = "1.5.9"
    val ffmpeg = "6.0-$javacpp"

    // Logging
    val slf4j = "2.0.12"
    val logback = "1.5.3"

    // Android-only
    val jvmTarget = JavaVersion.VERSION_11
    val androidTargetSdk = 34
    val androidMinSdk = 23
    val androidCompileSdk = androidTargetSdk

    // Linux-only
    val dbus = "4.3.1"
}
