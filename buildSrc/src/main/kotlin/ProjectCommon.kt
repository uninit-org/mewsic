
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.github.gmazzo.buildconfig.BuildConfigExtension
import com.github.gmazzo.buildconfig.generators.BuildConfigKotlinGenerator
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.*
import org.jetbrains.compose.internal.utils.getLocalProperty
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.target.Family
import java.util.*


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
        sourceCompatibility = Versions.javaTarget
        targetCompatibility = Versions.javaTarget
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
                project.rootProject.file("proguard/common-rules.pro"),
                project.rootProject.file("proguard/android-rules.pro"),
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

            val linuxNativeMain by creating {
                dependsOn(desktopNativeMain)
            }

            val linuxX64Main by getting {
                dependsOn(linuxNativeMain)
            }

//            val linuxArm64Main by getting {
//                dependsOn(linuxNativeMain)
//            }

//            val mingwX64Main by getting {
//                dependsOn(desktopNativeMain)
//            }

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

    configure<BuildConfigExtension> {
        generator = BuildConfigKotlinGenerator()
        packageName = "${project.group}.buildconfig"
        className = "${project.name.removePrefix("mewsic-").split("-").joinToString("") { it.capitalize(Locale.getDefault()) }}BuildConfig"
        buildConfigField("kotlin.String", "VERSION", "\"${project.version}\"")
        buildConfigField("kotlin.Boolean", "IS_DEVELOPMENT", (getLocalProperty("development") ?: "false").toString())
    }

    with(tasks) {
        rootProject.tasks.named("prepareKotlinBuildScriptModel") {
            dependsOn(tasks.named("generateNonAndroidBuildConfig"))
        }

        withType<JavaCompile> {
            sourceCompatibility = Versions.javaTarget.toString()
            targetCompatibility = Versions.javaTarget.toString()
        }

        withType<KotlinCompile> {
            compilerOptions {
                jvmTarget = Versions.jvmTarget
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

fun KotlinMultiplatformExtension.nativeTargets(block: KotlinNativeTarget.() -> Unit) {
    targets.filterIsInstance<KotlinNativeTarget>().onEach(block)
}

fun KotlinMultiplatformExtension.linuxTargets(block: KotlinNativeTarget.() -> Unit) {
    nativeTargets {
        if (konanTarget.family == Family.LINUX) {
            block()
        }
    }
}

fun KotlinMultiplatformExtension.androidNativeTargets(block: KotlinNativeTarget.() -> Unit) {
    nativeTargets {
        if (konanTarget.family == Family.ANDROID) {
            block()
        }
    }
}
