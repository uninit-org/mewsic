import org.gradle.api.JavaVersion
import org.jetbrains.compose.ComposeBuildConfig
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object Versions {
    // Java/JVM
    val javaTarget = JavaVersion.VERSION_17
    val jvmTarget = JvmTarget.JVM_17

    // Kotlin core
    val kotlin = "2.0.0"
    val coroutines = "1.8.0"
    val serialization = "1.7.1"
    val datetime = "0.6.0"

    // Libraries
    val ktor = "2.3.12"

    // Compose
    val compose = ComposeBuildConfig.composeVersion
    val decompose = "3.1.0"
    val multiplatformSettings = "1.1.1"
    val activityCompose = "1.9.0"  // Android-only

    // Javacpp
    val javacpp = "1.5.9"
    val ffmpeg = "6.0-$javacpp"

    // Auto Service
    val autoService = "1.1.1"
    val autoServiceKsp = "1.2.0"

    // Logging
    val slf4j = "2.0.13"
    val logback = "1.5.6"

    // Android-only
    val androidTargetSdk = 34
    val androidMinSdk = 23
    val androidCompileSdk = androidTargetSdk

    // Linux-only
    val dbus = "5.0.0"
}
