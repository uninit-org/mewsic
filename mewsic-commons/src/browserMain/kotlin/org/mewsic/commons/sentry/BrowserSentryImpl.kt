package org.mewsic.commons.sentry

import externals.sentry.SentryOptions
import externals.sentry.captureException
import externals.sentry.init
import org.mewsic.commons.CommonsBuildConfig
import kotlin.js.json


object BrowserSentryImpl : Sentry {
    private var optedIn = true

    init {
        init(object : SentryOptions {
            override val dsn: String = CommonsBuildConfig.SENTRY_DSN
            override val release = CommonsBuildConfig.VERSION
            override val debug = CommonsBuildConfig.DEVELOPMENT
            override val attachStacktrace = true
            override val initialScope = json(
                "tags" to json(
                    "commit_count" to CommonsBuildConfig.COMMIT_COUNT.toString(),
                    "commit_sha" to CommonsBuildConfig.COMMIT_SHA,
                    "build_time" to CommonsBuildConfig.BUILD_TIME
                )
            )

        })
    }

    override fun optOut() {
        optedIn = false
    }

    override fun capture(e: Exception, tags: Map<String, String>) {
        if (optedIn) {
            captureException(e, json("tags" to tags))
        }
    }
}

actual fun getSentry(): Sentry = BrowserSentryImpl
