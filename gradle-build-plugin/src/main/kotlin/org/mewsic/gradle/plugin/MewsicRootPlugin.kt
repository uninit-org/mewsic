package org.mewsic.gradle.plugin

import com.github.gmazzo.gradle.plugins.BuildConfigExtension
import org.mewsic.gradle.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType

open class MewsicRootPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val commitCount = getCommitCount()
        val gitSha = getGitSha()
        val buildTime = getBuildTime()

        val build_version by defaultProperty(getGitTag() ?: "v1.0.0-${commitCount}-${gitSha}")
        version = build_version

        target.configure<ExtraPropertiesExtension> {
            this.set("buildconfig_entries", listOf(
                Triple("Int", "COMMIT_COUNT", commitCount),
                Triple("String", "COMMIT_SHA", "\"$gitSha\""),
                Triple("String", "BUILD_TIME", "\"$buildTime\""),
            ))
        }
    }
}
