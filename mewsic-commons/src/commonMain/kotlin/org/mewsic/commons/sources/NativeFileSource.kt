package org.mewsic.commons.sources

import org.mewsic.commons.streams.api.SeekableInputStream
import org.mewsic.commons.sources.api.Source

expect class NativeFileSource(path: String) : Source {
    override fun open(): SeekableInputStream
    override fun reset()
}
