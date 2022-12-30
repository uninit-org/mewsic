package net.sourceforge.jaad.mp4.boxes.impl.meta
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox

/**
 * This box contains the data for a metadata tag. It is right below an
 * iTunes metadata box (e.g. '@nam') or a custom meta tag box ('----'). A custom
 * meta tag box also contains a 'name'-box declaring the tag's name.
 *
 * @author in-somnia
 */
/*TODO: use generics here? -> each DataType should return <T> corresponding to
its class (String/Integer/...)*/
class ITunesMetadataBox : FullBox("iTunes Metadata Box") {
    enum class DataType {
        IMPLICIT, UTF8, UTF16, HTML, XML, UUID, ISRC, MI3P, GIF, JPEG, PNG, URL, DURATION, DATETIME, GENRE, INTEGER, RIAA, UPC, BMP, UNDEFINED;

        companion object {
            private val TYPES = arrayOf(
                IMPLICIT,
                UTF8,
                UTF16,
                null,
                null,
                null,
                HTML,
                XML,
                UUID,
                ISRC,
                MI3P,
                null,
                GIF,
                JPEG,
                PNG,
                URL,
                DURATION,
                DATETIME,
                GENRE,
                null,
                null,
                INTEGER,
                null,
                null,
                RIAA,
                UPC,
                null,
                BMP
            )

            fun forInt(i: Int): DataType {
                var type: DataType? = null
                if (i >= 0 && i < TYPES.size) type = TYPES[i]
                if (type == null) type = UNDEFINED
                return type
            }
        }
    }

    var dataType: DataType? = null
        private set
    private lateinit var data: ByteArray
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        dataType = DataType.forInt(flags)
        `in`.skipBytes(4) //padding?
        data = ByteArray(getLeft(`in`).toInt())
        `in`.readBytes(data)
    }

    /**
     * Returns an unmodifiable array with the raw content, that can be present
     * in different formats.
     *
     * @return the raw metadata
     */
    fun getData(): ByteArray {
        return Arrays.copyOf(data, data.size)
    }

    val text: String
        /**
         * Returns the content as a text string.
         * @return the metadata as text
         */
        get() =//first four bytes are padding (zero)
            data.decodeToString(0, data.size)
//            String(data, 0, data.size, java.nio.charset.Charset.forName("UTF-8"))
    val number: Long
        /**
         * Returns the content as an unsigned 8-bit integer.
         * @return the metadata as an integer
         */
        get() {
            //first four bytes are padding (zero)
            var l: Long = 0
            for (i in data.indices) {
                l = l shl 8
                l = l or (data[i].toInt() and 0xFF).toLong()
            }
            return l
        }
    val integer: Int
        get() = number.toInt()
    val boolean: Boolean
        /**
         * Returns the content as a boolean (flag) value.
         * @return the metadata as a boolean
         */
        get() = number != 0L
    val date: String?
        get() {
            //timestamp lengths: 4,7,9
            return when (data.size) { // FIXME: this is not correct
                4 -> {
                    val year = number.toInt()
                    if (year == 0) null else year.toString()
                }
                7 -> {
                    val year = number.toInt() shr 16
                    val month = number.toInt() shr 8 and 0xFF
                    val day = number.toInt() and 0xFF
                    if (year == 0) null else "$year-$month-$day"
                }
                9 -> {
                    val year = number.toInt() shr 16
                    val month = number.toInt() shr 8 and 0xFF
                    val day = number.toInt() and 0xFF
                    val hour = number.toInt() shr 24
                    val minute = number.toInt() shr 16 and 0xFF
                    val second = number.toInt() shr 8 and 0xFF
                    if (year == 0) null else "$year-$month-$day $hour:$minute:$second"
                }
                else -> null
            }
//            val i: Int = java.lang.Math.floor((data.size / 3).toDouble()).toInt() - 1
//            val date: java.util.Date?
//            date = if (i >= 0 && i < TIMESTAMPS.size) {
//                val sdf: java.text.SimpleDateFormat = java.text.SimpleDateFormat(TIMESTAMPS[i])
//                sdf.parse(kotlin.String(data), java.text.ParsePosition(0))
//            } else null
//            return date
        }

    companion object {
        private val TIMESTAMPS = arrayOf("yyyy", "yyyy-MM", "yyyy-MM-dd")
    }
}
