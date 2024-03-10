package dev.uninit.mewsic.utils.platform

import android.util.Log

actual object Logger : LoggerBase {
    override fun debug(message: String) {
        if (Log.isLoggable("Mewsic", Log.DEBUG)) {
            Log.d("Mewsic", message.joinTags())
        }
    }

    override fun debug(message: String, throwable: Throwable) {
        if (Log.isLoggable("Mewsic", Log.DEBUG)) {
            Log.d("Mewsic", message.joinTags(), throwable)
        }
    }

    override fun info(message: String) {
        if (Log.isLoggable("Mewsic", Log.INFO)) {
            Log.i("Mewsic", message.joinTags())
        }
    }

    override fun info(message: String, throwable: Throwable) {
        if (Log.isLoggable("Mewsic", Log.INFO)) {
            Log.i("Mewsic", message.joinTags(), throwable)
        }
    }

    override fun warn(message: String) {
        if (Log.isLoggable("Mewsic", Log.WARN)) {
            Log.w("Mewsic", message.joinTags())
        }
    }

    override fun warn(message: String, throwable: Throwable) {
        if (Log.isLoggable("Mewsic", Log.WARN)) {
            Log.w("Mewsic", message.joinTags(), throwable)
        }
    }

    override fun error(message: String) {
        if (Log.isLoggable("Mewsic", Log.ERROR)) {
            Log.e("Mewsic", message.joinTags())
        }
    }

    override fun error(message: String, throwable: Throwable) {
        if (Log.isLoggable("Mewsic", Log.ERROR)) {
            Log.e("Mewsic", message.joinTags(), throwable)
        }
    }

    override fun critical(message: String) {
        if (Log.isLoggable("Mewsic", Log.ASSERT)) {
            Log.wtf("Mewsic", message.joinTags())
        }
    }

    override fun critical(message: String, throwable: Throwable) {
        if (Log.isLoggable("Mewsic", Log.ASSERT)) {
            Log.wtf("Mewsic", message.joinTags(), throwable)
        }
    }
}
