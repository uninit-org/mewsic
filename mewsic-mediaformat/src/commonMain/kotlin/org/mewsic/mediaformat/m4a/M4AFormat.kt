package org.mewsic.mediaformat.m4a

import org.mewsic.commons.streams.api.InputStream
import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.mediaformat.api.MediaFormat

object M4AFormat : MediaFormat<M4AContainer> {
    override fun encode(container: M4AContainer): OutputStream {
        TODO("Not yet implemented")
    }

    override fun decode(inStream: InputStream): M4AContainer {
        TODO("Not yet implemented")
    }
}
