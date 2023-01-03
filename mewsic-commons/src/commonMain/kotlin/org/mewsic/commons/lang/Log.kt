package org.mewsic.commons.lang

expect object Log {
    actual enum class Level {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }
    actual var minLevel: Level
        private set

    fun setMinLevel(level: Level)
    fun log(level: Level, message: String)

//    fun debug(message: String) = log(Level.DEBUG, message)
//    fun info(message: String) = log(Level.INFO, message)
//    fun warn(message: String) = log(Level.WARN, message)
//    fun error(message: String) = log(Level.ERROR, message)
//    fun fatal(message: String) = log(Level.FATAL, message)
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String)
    fun fatal(message: String)


}
