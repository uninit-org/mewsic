package org.mewsic.mediaformat.webm

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.mediaformat.api.MediaContainer

class WebMContainer : MediaContainer<WebMMetadata> {
    override fun audio(offset: Int, length: Int): OutputStream {
        TODO("Not yet implemented")
    }

    override fun metadata(): WebMMetadata {
        TODO("Not yet implemented")
    }
}
