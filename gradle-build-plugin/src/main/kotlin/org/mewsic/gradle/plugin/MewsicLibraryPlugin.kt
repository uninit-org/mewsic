package org.mewsic.gradle.plugin

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.*
import org.gradle.kotlin.dsl.*
import org.mewsic.gradle.Constants
import java.util.*

open class MewsicLibraryPlugin : MewsicCommonPlugin {
    override fun apply(target: Project) = target.apply()

    @JvmName("apply0")
    private fun Project.apply() {
        applyCommon()
    }

    override fun Project.setupAndroid() {
        apply<LibraryPlugin>()

        configure<LibraryExtension> {
            compileSdk = Constants.Android.COMPILE_SDK

            defaultConfig {
                minSdk = Constants.Android.MIN_SDK
                targetSdk = Constants.Android.TARGET_SDK
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            sourceSets {
                named("main") {
                    manifest.srcFile("src/androidMain/AndroidManifest.xml")
                    res.srcDirs("src/androidMain/res")
                }
            }
        }
    }
}
