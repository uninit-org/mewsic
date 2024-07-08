package dev.uninit.mewsic.utils.platform

interface LoggerBase {
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String)
    fun critical(message: String)

    fun debug(message: String, throwable: Throwable) {
        debug(message)
        debug(throwable.stackTraceToString())
    }
    fun info(message: String, throwable: Throwable) {
        info(message)
        info(throwable.stackTraceToString())
    }
    fun warn(message: String, throwable: Throwable) {
        warn(message)
        warn(throwable.stackTraceToString())
    }
    fun error(message: String, throwable: Throwable) {
        error(message)
        error(throwable.stackTraceToString())
    }
    fun critical(message: String, throwable: Throwable) {
        critical(message)
        critical(throwable.stackTraceToString())
    }

    fun withPrefix(prefix: String): LoggerBase {
        return LoggerWrapper(prefix, this)
    }
}

expect object Logger : LoggerBase

/**
 * A logger that outputs to stdout and stderr.
 */
abstract class OutputLogger : LoggerBase {
    abstract fun stdout(message: String)
    abstract fun stderr(message: String)

    override fun debug(message: String) {
        stdout("DEBUG: ${message.joinTags()}")
    }

    override fun info(message: String) {
        stdout("INFO: ${message.joinTags()}")
    }

    override fun warn(message: String) {
        stderr("WARN: ${message.joinTags()}}")
    }

    override fun error(message: String) {
        stderr("ERROR: ${message.joinTags()}")
    }

    override fun critical(message: String) {
        stderr("CRITICAL: ${message.joinTags()}")
    }
}

class LoggerWrapper(private val prefix: String, private val wrapped: LoggerBase) : LoggerBase {
    override fun debug(message: String) {
        wrapped.debug("$prefix $message")
    }

    override fun debug(message: String, throwable: Throwable) {
        wrapped.debug("$prefix $message", throwable)
    }

    override fun info(message: String) {
        wrapped.info("$prefix $message")
    }

    override fun info(message: String, throwable: Throwable) {
        wrapped.info("$prefix $message", throwable)
    }

    override fun warn(message: String) {
        wrapped.warn("$prefix $message")
    }

    override fun warn(message: String, throwable: Throwable) {
        wrapped.warn("$prefix $message", throwable)
    }

    override fun error(message: String) {
        wrapped.error("$prefix $message")
    }

    override fun error(message: String, throwable: Throwable) {
        wrapped.error("$prefix $message", throwable)
    }

    override fun critical(message: String) {
        wrapped.critical("$prefix $message")
    }

    override fun critical(message: String, throwable: Throwable) {
        wrapped.critical("$prefix $message", throwable)
    }
}

internal fun String.joinTags(): String {
    // Converts "[a] [b] [c] stuff [d] [e]" to "[a | b | c] stuff [d] [e]"
    val pattern = Regex("""^(\[[^]]+])\s+(\[[^]]+])""")
    var result = this
    while (true) {
        val match = pattern.find(result) ?: break
        val (first, second) = match.destructured
        result = result.replaceFirst(match.value, "[${first.substring(1, first.lastIndex)} | ${second.substring(1, second.lastIndex)}]")
    }
    return result
}

fun Any.makeLogger(): LoggerBase {
    val namespaceParts = (this::class.qualifiedName ?: this::class.simpleName ?: this::class.toString()).split(".").toMutableList()
    val className = namespaceParts.removeLast()
    val namespace = namespaceParts.joinToString(".") { if (it.first().isLowerCase()) it.first().toString() else it }
    val prefix = if (namespace.isBlank()) className else "$namespace.$className"
    return Logger.withPrefix("[$prefix]")
}
