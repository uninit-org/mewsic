package dev.uninit.mewsic.utils.platform

import org.slf4j.LoggerFactory

actual object Logger : LoggerBase {
    private val log = LoggerFactory.getLogger("Mewsic")

    override fun debug(message: String) {
        log.debug(message.joinTags())
    }

    override fun debug(message: String, throwable: Throwable) {
        log.debug(message.joinTags(), throwable)
    }

    override fun info(message: String) {
        log.info(message.joinTags())
    }

    override fun info(message: String, throwable: Throwable) {
        log.info(message.joinTags(), throwable)
    }

    override fun warn(message: String) {
        log.warn(message.joinTags())
    }

    override fun warn(message: String, throwable: Throwable) {
        log.warn(message.joinTags(), throwable)
    }

    override fun error(message: String) {
        log.error(message.joinTags())
    }

    override fun error(message: String, throwable: Throwable) {
        log.error(message.joinTags(), throwable)
    }

    override fun critical(message: String) {
        log.error(message.joinTags())
    }

    override fun critical(message: String, throwable: Throwable) {
        log.error(message.joinTags(), throwable)
    }
}
