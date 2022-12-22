package org.mewsic.commons.streams

import org.mewsic.commons.streams.api.InputStream
import org.mewsic.commons.streams.api.OutputStream

fun JInputStream.toCommonInputStream(): InputStream {
    return JavaInputStream(this)
}

fun JOutputStream.toCommonOutputStream(): OutputStream {
    return JavaOutputStream(this)
}
