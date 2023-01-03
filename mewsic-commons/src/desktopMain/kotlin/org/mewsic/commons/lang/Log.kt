package org.mewsic.commons.lang

actual object Log {
    actual enum class Level {
        DEBUG, INFO, WARN, ERROR, FATAL
    }

    actual var minLevel: Level = Level.DEBUG
        private set

    actual fun setMinLevel(level: Level) {
        minLevel = level
    }

    actual fun log(level: Level, message: String) {
        if (level.ordinal >= minLevel.ordinal) {
            println("[${level.name}] $message")
        }
    }

    actual fun debug(message: String) = log(Level.DEBUG, message)
    actual fun info(message: String) = log(Level.INFO, message)
    actual fun warn(message: String) = log(Level.WARN, message)
    actual fun error(message: String) = log(Level.ERROR, message)
    actual fun fatal(message: String) = log(Level.FATAL, message)


}
