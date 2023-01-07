package org.mewsic.kmedia.container.mpeg4.decode

import org.mewsic.commons.streams.ByteArrayInputStream
import org.mewsic.commons.streams.DataInputStream

open class Box(buffer: ByteArray) {
    val input = DataInputStream(ByteArrayInputStream(buffer))

    companion object {

    }

}
