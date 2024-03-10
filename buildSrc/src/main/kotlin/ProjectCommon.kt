
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


private fun TestedExtension.configureCommon(project: Project) {
    compileSdkVersion(Versions.androidCompileSdk)
    ndkVersion = "21.4.7075529"
    namespace = "${project.group}.${project.name.replace('-', '.')}"

    defaultConfig {
        minSdk = Versions.androidMinSdk
        targetSdk = Versions.androidTargetSdk
    }

    signingConfigs {
        create("release") {
            storeFile = project.file(System.getenv("KEYSTORE_FILE") ?: "keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    compileOptions {
        sourceCompatibility = Versions.jvmTarget
        targetCompatibility = Versions.jvmTarget
    }

    buildTypes {
        named("debug") {
            versionNameSuffix = "-debug"
            proguardFiles(
                getDefaultProguardFile("proguard-defaults.txt"),
                project.rootProject.file("proguard/android-rules.pro")
            )
        }

        named("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                project.rootProject.file("proguard/android-rules.pro")
            )
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = false
        }
    }

    (this as? BaseAppModuleExtension)?.let {
        defaultConfig {
            applicationId = namespace
            versionName = project.version.toString()
            versionCode = project.version.toString().split(".").map { it.toInt() }.reduce { acc, i -> acc shl 8 + i }
        }

        buildTypes {
            named("release") {
                isShrinkResources = true
            }
        }
    }
}


fun Project.configureCommon() {
    configure<KotlinMultiplatformExtension> {
        jvm("desktop")
        androidTarget()
        linuxX64()
        linuxArm64()
        mingwX64()
        androidNativeArm64()
        androidNativeArm32()
        androidNativeX64()
        androidNativeX86()

        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}")
                }
            }

            val appMain by creating {
                dependsOn(commonMain)
            }

            val desktopMain by getting {
                dependsOn(appMain)
            }

            val androidMain by getting {
                dependsOn(appMain)
            }

            val nativeMain by creating {
                dependsOn(commonMain)
            }

            val desktopNativeMain by creating {
                dependsOn(nativeMain)
            }

            val linuxX64Main by getting {
                dependsOn(desktopNativeMain)
            }

            val linuxArm64Main by getting {
                dependsOn(desktopNativeMain)
            }

            val mingwX64Main by getting {
                dependsOn(desktopNativeMain)
            }

            val androidNativeMain by creating {
                dependsOn(nativeMain)
            }

            val androidNativeArm64Main by getting {
                dependsOn(androidNativeMain)
            }

            val androidNativeArm32Main by getting {
                dependsOn(androidNativeMain)
            }

            val androidNativeX64Main by getting {
                dependsOn(androidNativeMain)
            }

            val androidNativeX86Main by getting {
                dependsOn(androidNativeMain)
            }
        }
    }

    extensions.findByType<BaseAppModuleExtension>()?.configureCommon(this)
    extensions.findByType<LibraryExtension>()?.configureCommon(this)

    with(tasks) {
        withType<JavaCompile> {
            sourceCompatibility = Versions.jvmTarget.toString()
            targetCompatibility = Versions.jvmTarget.toString()
        }

        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = Versions.jvmTarget.toString()
            }
        }
    }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.appMain(configure: KotlinSourceSet.() -> Unit) {
    val appMain by getting {
        configure()
    }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.desktopMain(configure: KotlinSourceSet.() -> Unit) {
    val desktopMain by getting {
        configure()
    }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.nativeMain(configure: KotlinSourceSet.() -> Unit) {
    val nativeMain by getting {
        configure()
    }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.desktopNativeMain(configure: KotlinSourceSet.() -> Unit) {
    val desktopNativeMain by getting {
        configure()
    }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.androidNativeMain(configure: KotlinSourceSet.() -> Unit) {
    val androidNativeMain by getting {
        configure()
    }
}
