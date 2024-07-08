package dev.uninit.mewsic.utils.platform

import java.awt.Desktop
import java.net.URI
import java.util.*

open class DesktopPlatform : Platform {
    enum class OperatingSystem {
        WINDOWS,
        MAC,
        LINUX,
        SOLARIS,
        UNKNOWN,
    }

    private val logger = makeLogger()
    private val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
    private val osType = when {
        osName.contains("win") -> OperatingSystem.WINDOWS
        osName.contains("mac") -> OperatingSystem.MAC
        osName.contains("solaris") || osName.contains("sunos") -> OperatingSystem.SOLARIS
        osName.contains("linux") || osName.contains("unix") -> OperatingSystem.LINUX
        else -> OperatingSystem.UNKNOWN
    }

    override fun openUrl(uri: URI): Boolean {
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri)
                return true
            }
        }

        return openFallback(uri.toString())
    }
    
    fun openFallback(resource: String): Boolean {
        when (osType) {
            OperatingSystem.LINUX -> {
                if (runCommand("xdg-open", resource)) return true
                if (runCommand("gnome-open", resource)) return true
                if (runCommand("kde-open", resource)) return true
            }
            OperatingSystem.MAC -> {
                if (runCommand("open", resource)) return true
            }
            OperatingSystem.WINDOWS -> {
                if (runCommand("explorer", resource)) return true
            }
            else -> {
                logger.error("Unsupported operating system: $osName")
                return false
            }
        }

        logger.error("Failed to open resource: $resource")
        return false
    }

    fun runCommand(vararg command: String): Boolean {
        try {
            val process = ProcessBuilder(*command).start()
            process.waitFor()
            return process.exitValue() == 0
        } catch (e: Exception) {
            logger.error("Failed to run command", e)
            return false
        }
    }
}
