package org.mewsic.commons.sources

import org.mewsic.commons.streams.api.SeekableInputStream
import org.mewsic.commons.sources.api.Source

actual class NativeFileSource actual constructor(path: String) : Source {
    init {
        js("fuckery");


    }
    actual override fun open(): SeekableInputStream {
        TODO("Not yet implemented")
    }

    actual override fun reset() {
    }
}
