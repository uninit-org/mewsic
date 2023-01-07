package org.mewsic.commons.lang

import android.util.Log

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
        if (alsoHandler != null) {
            alsoHandler?.handle(level, message)
        }
        if (level.ordinal >= minLevel.ordinal) {
            when (level) {
                Level.DEBUG -> Log.d("com/mewsic/commons::Log", message)
                Level.INFO -> Log.i("com/mewsic/commons::Log", message)
                Level.WARN -> Log.w("com/mewsic/commons::Log", message)
                Level.ERROR -> Log.e("com/mewsic/commons::Log", message)
                Level.FATAL -> Log.wtf("com/mewsic/commons::Log", message)
            }
        }
    }

    actual fun debug(message: String) = log(Level.DEBUG, message)
    actual fun info(message: String) = log(Level.INFO, message)
    actual fun warn(message: String) = log(Level.WARN, message)
    actual fun error(message: String) = log(Level.ERROR, message)
    actual fun fatal(message: String) = log(Level.FATAL, message)
    actual fun interface AlsoHandler {
        actual fun handle(level: Level, message: String)
    }

    actual var alsoHandler: AlsoHandler? = null


}
