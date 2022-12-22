package org.mewsic.mediaformat.m4a

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.mediaformat.api.MediaContainer

class M4AContainer : MediaContainer<M4AMetadata> {
    override fun audio(offset: Int, length: Int): OutputStream {
        TODO("Not yet implemented")
    }

    override fun metadata(): M4AMetadata {
        TODO("Not yet implemented")
    }
}
