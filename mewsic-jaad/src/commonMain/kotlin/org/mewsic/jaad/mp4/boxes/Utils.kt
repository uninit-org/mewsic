package org.mewsic.jaad.mp4.boxes

object Utils {
    private const val UNDETERMINED = 4294967295L
    fun getLanguageCode(l: Long): String {
        //1 bit padding, 5*3 bits language code (ISO-639-2/T)
        val c = CharArray(3)
        c[0] = Char(((l shr 10 and 31L) + 0x60).toUShort())
        c[1] = Char(((l shr 5 and 31L) + 0x60).toUShort())
        c[2] = Char(((l and 31L) + 0x60).toUShort())
        return c.concatToString()
    }

    fun detectUndetermined(l: Long): Long {
        val x: Long
        x = if (l == UNDETERMINED) -1 else l
        return x
    }
}
