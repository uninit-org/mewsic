@file:JsModule("@sentry/browser")
@file:JsNonModule

package externals.sentry

external fun init(options: SentryOptions)

external interface SentryOptions {
    val dsn: String
    val release: String
    val debug: Boolean
    val attachStacktrace: Boolean
    val initialScope: dynamic
}

external fun captureException(e: Exception, data: dynamic)
