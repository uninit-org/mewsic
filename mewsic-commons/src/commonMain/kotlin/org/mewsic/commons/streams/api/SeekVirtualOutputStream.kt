package org.mewsic.commons.streams.api

import org.mewsic.commons.lang.ExperimentalCacheApi

@ExperimentalCacheApi
abstract class SeekVirtualOutputStream : SeekVirtual(), OutputStream {
    override fun virtualizeStream(): SeekableOutputStream {
        TODO()
    }
}

