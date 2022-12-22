package org.mewsic.mediaformat.api

import org.mewsic.commons.streams.api.InputStream
import org.mewsic.commons.streams.api.OutputStream

interface MediaFormat<T: MediaContainer<*>> {
    fun encode(container: T): OutputStream
    fun decode(inStream: InputStream): T
}
