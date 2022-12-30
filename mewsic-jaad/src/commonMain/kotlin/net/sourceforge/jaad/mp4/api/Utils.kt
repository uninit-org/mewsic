package net.sourceforge.jaad.mp4.api
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
internal object Utils {
    private const val DATE_OFFSET = 2082850791998L
    fun getDate(time: Long): Long {
        return time * 1000 - DATE_OFFSET
    }
}
