package org.mewsic.commons.sentry

import org.mewsic.commons.CommonsBuildConfig
import io.sentry.Sentry as SentryImpl;

object JvmSentryImpl : Sentry {
    private var optedIn = true

    init {
        SentryImpl.init {
            it.dsn = CommonsBuildConfig.SENTRY_DSN
            it.release = CommonsBuildConfig.VERSION
            it.isDebug = CommonsBuildConfig.DEVELOPMENT
            it.isAttachStacktrace = true

            it.setTag("commit_count", CommonsBuildConfig.COMMIT_COUNT.toString())
            it.setTag("commit_sha", CommonsBuildConfig.COMMIT_SHA)
            it.setTag("build_time", CommonsBuildConfig.BUILD_TIME)
        }
    }

    override fun optOut() {
        optedIn = false
    }

    override fun capture(e: Exception, tags: Map<String, String>) {
        if (optedIn) {
            SentryImpl.captureException(e) {
                for ((key, value) in tags) {
                    it.setTag(key, value)
                }
            }
        }
    }
}

actual fun getSentry(): Sentry = JvmSentryImpl
