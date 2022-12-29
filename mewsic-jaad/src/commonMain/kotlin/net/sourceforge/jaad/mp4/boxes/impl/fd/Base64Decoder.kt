package net.sourceforge.jaad.mp4.boxes.impl.fd

/**
 * A BASE64 character decoder.
 */
internal object Base64Decoder {
    /*private static final char[] CHAR_ARRAY = {
	//       0   1   2   3   4   5   6   7
	'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', // 0
	'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', // 1
	'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', // 2
	'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', // 3
	'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', // 4
	'o', 'p', 'q', 'r', 's', 't', 'u', 'v', // 5
	'w', 'x', 'y', 'z', '0', '1', '2', '3', // 6
	'4', '5', '6', '7', '8', '9', '+', '/' // 7
	};*/
    //CHAR_CONVERT_ARRAY[CHAR_ARRAY[i]] = i;
    private val CHAR_CONVERT_ARRAY = byteArrayOf(
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57,
        58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8,
        9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1,
        -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38,
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, 0
    )

    fun decode(b: ByteArray?): ByteArray {
        val `in`: java.io.ByteArrayInputStream = java.io.ByteArrayInputStream(b)
        val out: java.io.ByteArrayOutputStream = java.io.ByteArrayOutputStream()
        var i: Int
        val ps: java.io.PushbackInputStream = java.io.PushbackInputStream(`in`)
        try {
            while (true) {
                i = 0
                while (i + 4 < 72) {
                    decodeAtom(ps, out, 4)
                    i += 4
                }
                if (i + 4 == 72) decodeAtom(ps, out, 4) else decodeAtom(ps, out, 72 - i)
            }
        } catch (e: java.io.IOException) {
        }
        return out.toByteArray()
    }

    @Throws(java.io.IOException::class)
    private fun decodeAtom(`in`: java.io.InputStream, out: java.io.OutputStream, rem: Int) {
        var rem = rem
        if (rem < 2) throw java.io.IOException()
        var i: Int
        do {
            i = `in`.read()
            if (i == -1) throw java.io.IOException()
        } while (i == '\n'.code || i == '\r'.code)
        val buf = ByteArray(4)
        buf[0] = i.toByte()
        i = readFully(`in`, buf, 1, rem - 1)
        if (i == -1) throw java.io.IOException()
        if (rem > 3 && buf[3] == '='.code.toByte()) rem = 3
        if (rem > 2 && buf[2] == '='.code.toByte()) rem = 2
        var a: Byte = -1
        var b: Byte = -1
        var c: Byte = -1
        var d: Byte = -1
        when (rem) {
            4 -> {
                d = CHAR_CONVERT_ARRAY[buf[3].toInt() and 0xff]
                c = CHAR_CONVERT_ARRAY[buf[2].toInt() and 0xff]
                b = CHAR_CONVERT_ARRAY[buf[1].toInt() and 0xff]
                a = CHAR_CONVERT_ARRAY[buf[0].toInt() and 0xff]
            }

            3 -> {
                c = CHAR_CONVERT_ARRAY[buf[2].toInt() and 0xff]
                b = CHAR_CONVERT_ARRAY[buf[1].toInt() and 0xff]
                a = CHAR_CONVERT_ARRAY[buf[0].toInt() and 0xff]
            }

            2 -> {
                b = CHAR_CONVERT_ARRAY[buf[1].toInt() and 0xff]
                a = CHAR_CONVERT_ARRAY[buf[0].toInt() and 0xff]
            }
        }
        when (rem) {
            2 -> out.write((a.toInt() shl 2 and 0xfc or (b.toInt() ushr 4 and 3)).toByte().toInt())
            3 -> {
                out.write((a.toInt() shl 2 and 0xfc or (b.toInt() ushr 4 and 3)).toByte().toInt())
                out.write((b.toInt() shl 4 and 0xf0 or (c.toInt() ushr 2 and 0xf)).toByte().toInt())
            }

            4 -> {
                out.write((a.toInt() shl 2 and 0xfc or (b.toInt() ushr 4 and 3)).toByte().toInt())
                out.write((b.toInt() shl 4 and 0xf0 or (c.toInt() ushr 2 and 0xf)).toByte().toInt())
                out.write((c.toInt() shl 6 and 0xc0 or (d.toInt() and 0x3f)).toByte().toInt())
            }
        }
        return
    }

    @Throws(java.io.IOException::class)
    private fun readFully(`in`: java.io.InputStream, b: ByteArray, off: Int, len: Int): Int {
        for (i in 0 until len) {
            val q: Int = `in`.read()
            if (q == -1) return if (i == 0) -1 else i
            b[i + off] = q.toByte()
        }
        return len
    }
}
