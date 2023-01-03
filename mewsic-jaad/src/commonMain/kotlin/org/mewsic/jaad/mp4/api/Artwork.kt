package net.sourceforge.jaad.mp4.api

import net.sourceforge.jaad.mp4.api.Artwork.Type
import net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataBox
import org.mewsic.commons.streams.ByteArrayInputStream

class Artwork internal constructor(
    /**
     * Returns the type of data in this artwork.
     *
     * @see Type
     *
     * @return the data's type
     */
    val type: Type?,
    /**
     * Returns the encoded data of this artwork.
     *
     * @return the encoded data
     */
    val data: ByteArray
) {
    //TODO: need this enum? it just copies the DataType
    enum class Type {
        GIF, JPEG, PNG, BMP;

        companion object {
            fun forDataType(dataType: ITunesMetadataBox.DataType?) = when (dataType) {
                ITunesMetadataBox.DataType.GIF -> GIF
                ITunesMetadataBox.DataType.JPEG -> JPEG
                ITunesMetadataBox.DataType.PNG -> PNG
                ITunesMetadataBox.DataType.BMP -> BMP
                else -> null
            }
        }
    }

    @get:Throws(Exception::class)
    // FIXME: No impl for image in multiplatform
    var image: ByteArrayInputStream? = null
        /**
         * Returns the decoded image, that can be painted.
         *
         * @return the decoded image
         * @throws Exception if decoding fails
         */
        get() = try {
            if (field == null) field = ByteArrayInputStream(data)
            field
        } catch (e: Exception) {
            throw e
        }
        private set
}
