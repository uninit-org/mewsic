package net.sourceforge.jaad.mp4.api

internal object Utils {
    private const val DATE_OFFSET = 2082850791998L
    fun getDate(time: Long): java.util.Date {
        return java.util.Date(time * 1000 - DATE_OFFSET)
    }
}
