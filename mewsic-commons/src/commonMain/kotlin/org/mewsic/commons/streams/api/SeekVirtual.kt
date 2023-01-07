package org.mewsic.commons.streams.api

import org.mewsic.commons.lang.ExperimentalCacheApi
import org.mewsic.commons.raw.LinkedByteChunk
/**
 * Virtualize seekability whilst not running out of memory by reading the entire stream into a [LinkedByteChunk]
 */
@ExperimentalCacheApi
abstract class SeekVirtual  {
    @ExperimentalCacheApi
    abstract fun virtualizeStream(): Seekable
}
