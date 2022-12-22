package org.mewsic.mediaformat.api

import org.mewsic.commons.streams.api.OutputStream

interface MediaContainer<T : MediaMetadata> {
    fun audio(offset: Int = 0, length: Int = -1): OutputStream
    fun metadata(): T
}
