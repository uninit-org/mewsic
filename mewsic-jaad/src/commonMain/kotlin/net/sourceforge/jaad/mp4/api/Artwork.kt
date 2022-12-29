package net.sourceforge.jaad.mp4.api

import net.sourceforge.jaad.mp4.api.Artwork.Typeimport

net.sourceforge.jaad.mp4.boxes.impl .meta.ITunesMetadataBox.DataType
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
            fun forDataType(dataType: DataType?): Type? {
                val type: Type?
                type = when (dataType) {
                    GIF -> GIF
                    JPEG -> JPEG
                    PNG -> PNG
                    BMP -> BMP
                    else -> null
                }
                return type
            }
        }
    }

    @get:Throws(java.io.IOException::class)
    var image: java.awt.Image? = null
        /**
         * Returns the decoded image, that can be painted.
         *
         * @return the decoded image
         * @throws IOException if decoding fails
         */
        get() = try {
            if (field == null) field = ImageIO.read(java.io.ByteArrayInputStream(data))
            field
        } catch (e: java.io.IOException) {
            java.util.logging.Logger.getLogger("MP4 API")
                .log(java.util.logging.Level.SEVERE, "Artwork.getImage failed: {0}", e.toString())
            throw e
        }
        private set
}
