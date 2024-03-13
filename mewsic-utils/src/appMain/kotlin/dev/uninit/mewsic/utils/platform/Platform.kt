package dev.uninit.mewsic.utils.platform

import java.net.URI

interface Platform {
    fun openUrl(uri: URI): Boolean
}
