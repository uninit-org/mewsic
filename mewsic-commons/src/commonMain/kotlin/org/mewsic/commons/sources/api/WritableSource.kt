package org.mewsic.commons.sources.api

import org.mewsic.commons.streams.api.SeekableOutputStream

interface WritableSource : Source {
    fun openForWrite(): SeekableOutputStream

}
