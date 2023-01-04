package org.mewsic.commons.sources

import org.mewsic.commons.sources.api.Source
import org.mewsic.commons.streams.ByteArrayInputStream
import org.mewsic.commons.streams.api.SeekableInputStream

class ByteArraySource(private val byteArray: ByteArray) : Source {
    override fun open(): SeekableInputStream {
        return ByteArrayInputStream(byteArray.copyOf())
    }

    override fun reset() {
        // do nothing
    }

}
