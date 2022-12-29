package org.mewsic.commons.sentry

interface Sentry {
    // On start, ask for permission to send crash reports
    // If the user says no, call optOut and save this setting somewhere
    fun optOut()
    fun capture(e: Exception, tags: Map<String, String> = emptyMap())
}

expect fun getSentry(): Sentry
