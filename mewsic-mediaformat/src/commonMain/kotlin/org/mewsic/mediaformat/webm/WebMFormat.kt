package org.mewsic.mediaformat.webm

import org.mewsic.commons.streams.api.InputStream
import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.mediaformat.api.MediaFormat

object WebMFormat : MediaFormat<WebMContainer> {
    override fun encode(container: WebMContainer): OutputStream {
        TODO("Not yet implemented")
    }

    override fun decode(inStream: InputStream): WebMContainer {
        TODO("Not yet implemented")
    }
}
