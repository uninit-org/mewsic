package dev.uninit.mewsic.utils.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.fflush
import platform.posix.fprintf

actual object Logger : OutputLogger(), LoggerBase {
    @ExperimentalForeignApi
    override fun stdout(message: String) {
        fprintf(platform.posix.stdout, "[Mewsic] %s\n", message)
        fflush(platform.posix.stdout)
    }

    @ExperimentalForeignApi
    override fun stderr(message: String) {
        fprintf(platform.posix.stderr, "[Mewsic] %s\n", message)
        fflush(platform.posix.stderr)
    }
}
