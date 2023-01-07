package org.mewsic.commons.lang

import kotlin.js.Console

actual object Log {
    private val console = js("console") as Console

    actual enum class Level {
        DEBUG, INFO, WARN, ERROR, FATAL
    }

    actual var minLevel: Level = Level.DEBUG
        private set

    actual fun log(level: Level, message: String) {
        if (alsoHandler != null) {
            alsoHandler?.handle(level, message)
        }
        when (level) {
            Level.DEBUG -> console.info(message)
            Level.INFO -> console.log(message)
            Level.WARN -> console.warn(message)
            Level.ERROR, Level.FATAL -> console.error(message)
        }

    }

    actual fun debug(message: String) = log(Level.DEBUG, message)
    actual fun info(message: String) = log(Level.INFO, message)
    actual fun warn(message: String) = log(Level.WARN, message)
    actual fun error(message: String) = log(Level.ERROR, message)
    actual fun fatal(message: String) = log(Level.FATAL, message)

    actual fun setMinLevel(level: Level) {
        minLevel = level
    }

    actual fun interface AlsoHandler {
        actual fun handle(level: Level, message: String)
    }

    actual var alsoHandler: AlsoHandler? = null
}
