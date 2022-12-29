package org.mewsic.commons.sources.api

import org.mewsic.commons.streams.api.SeekableInputStream

interface Source {
    fun open(): SeekableInputStream
    fun reset()
}
