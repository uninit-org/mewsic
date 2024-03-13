package dev.uninit.mewsic.utils.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URI

class AndroidPlatform(private val context: Context) : Platform {
    override fun openUrl(uri: URI): Boolean {
        return try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.fromParts(uri.scheme, uri.schemeSpecificPart, uri.fragment)))
            true
        } catch (e: Exception) {
            false
        }
    }

}
