package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

class ItemInformationEntry : FullBox("Item Information Entry") {
    /**
     * The item ID contains either 0 for the primary resource (e.g., the XML
     * contained in an 'xml ' box) or the ID of the item for which the following
     * information is defined.
     *
     * @return the item ID
     */
    var itemID = 0
        private set

    /**
     * The item protection index contains either 0 for an unprotected item, or
     * the one-based index into the item protection box defining the protection
     * applied to this item (the first box in the item protection box has the
     * index 1).
     *
     * @return the item protection index
     */
    var itemProtectionIndex = 0
        private set

    /**
     * The item name is a String containing a symbolic name of the item (source
     * file for file delivery transmissions).
     *
     * @return the item name
     */
    var itemName: String? = null
        private set

    /**
     * The content type is a String with the MIME type of the item. If the item
     * is content encoded (see below), then the content type refers to the item
     * after content decoding.
     *
     * @return the content type
     */
    var contentType: String? = null
        private set

    /**
     * The content encoding is an optional String used to indicate that the
     * binary file is encoded and needs to be decoded before interpreted. The
     * values are as defined for Content-Encoding for HTTP/1.1. Some possible
     * values are "gzip", "compress" and "deflate". An empty string indicates no
     * content encoding. Note that the item is stored after the content encoding
     * has been applied.
     *
     * @return the content encoding
     */
    var contentEncoding: String? = null
        private set

    /**
     * The extension type is a printable four-character code that identifies the
     * extension fields of version 1 with respect to version 0 of the item
     * information entry.
     *
     * @return the extension type
     */
    var extensionType: Long = 0
        private set

    /**
     * Returns the extension.
     */
    var extension: Extension? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        if (version == 0 || version == 1) {
            itemID = `in`.readBytes(2).toInt()
            itemProtectionIndex = `in`.readBytes(2).toInt()
            itemName = `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8)
            contentType = `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8)
            contentEncoding = `in`.readUTFString(getLeft(`in`).toInt(), MP4InputStream.UTF8) //optional
        }
        if (version == 1 && getLeft(`in`) > 0) {
            //optional
            extensionType = `in`.readBytes(4)
            if (getLeft(`in`) > 0) {
                extension = Extension.forType(extensionType.toInt())
                if (extension != null) extension!!.decode(`in`)
            }
        }
    }

    abstract class Extension {
        @Throws(Exception::class)
        abstract fun decode(`in`: MP4InputStream)

        companion object {
            private const val TYPE_FDEL = 1717855596 //fdel
            fun forType(type: Int): Extension? {
                val ext: Extension?
                ext = when (type) {
                    TYPE_FDEL -> FDExtension()
                    else -> null
                }
                return ext
            }
        }
    }

    class FDExtension : Extension() {
        /**
         * The content location is a String in containing the URI of the file as
         * defined in HTTP/1.1 (RFC 2616).
         *
         * @return the content location
         */
        var contentLocation: String? = null
            private set

        /**
         * The content MD5 is a string containing an MD5 digest of the file. See
         * HTTP/1.1 (RFC 2616) and RFC 1864.
         *
         * @return the content MD5
         */
        var contentMD5: String? = null
            private set

        /**
         * The total length (in bytes) of the (un-encoded) file.
         *
         * @return the content length
         */
        var contentLength: Long = 0
            private set

        /**
         * The transfer length is the total length (in bytes) of the (encoded)
         * file. Note that transfer length is equal to content length if no
         * content encoding is applied (see above).
         *
         * @return the transfer length
         */
        var transferLength: Long = 0
            private set

        /**
         * The group ID indicates a file group to which the file item (source
         * file) belongs. See 3GPP TS 26.346 for more details on file groups.
         *
         * @return the group IDs
         */
        lateinit var groupID: LongArray
            private set

        @Throws(Exception::class)
        override fun decode(`in`: MP4InputStream) {
            contentLocation = `in`.readUTFString(100, MP4InputStream.UTF8)
            contentMD5 = `in`.readUTFString(100, MP4InputStream.UTF8)
            contentLength = `in`.readBytes(8)
            transferLength = `in`.readBytes(8)
            val entryCount: Int = `in`.read()
            groupID = LongArray(entryCount)
            for (i in 0 until entryCount) {
                groupID[i] = `in`.readBytes(4)
            }
        }
    }
}
