package org.mewsic.kmedia.container.mpeg4.decode.boxes

import org.mewsic.commons.streams.api.SeekableInputStream
import kotlin.concurrent.thread

actual fun Box.fastNativeRead(seekable: SeekableInputStream) {
    thread {
        // Right here, we can be 100% sure that
        // the stream we're accessing has random access.
        // we can use this to our advantage
        val bufferRange = this.offset until this.offset + this.size

        if (this.hasChildren()) {

        }
    }
}
