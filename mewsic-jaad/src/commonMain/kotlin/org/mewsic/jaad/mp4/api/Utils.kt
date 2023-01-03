package org.mewsic.jaad.mp4.api

internal object Utils {
    private const val DATE_OFFSET = 2082850791998L
    fun getDate(time: Long): Long {
        return time * 1000 - DATE_OFFSET
    }
}
