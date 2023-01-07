package org.mewsic.commons.streams.api

import org.mewsic.commons.lang.ExperimentalCacheApi


@ExperimentalCacheApi
abstract class SeekVirtualInputStream : SeekVirtual(), InputStream {
    override fun virtualizeStream(): SeekableInputStream {
        TODO()
    }
}
