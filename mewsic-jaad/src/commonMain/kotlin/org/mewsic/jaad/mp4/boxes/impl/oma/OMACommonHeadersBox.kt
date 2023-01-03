package net.sourceforge.jaad.mp4.boxes.impl.oma

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox

//TODO: add remaining javadoc
class OMACommonHeadersBox : FullBox("OMA DRM Common Header Box") {
    /**
     * The encryption method defines how the encrypted content can be decrypted.
     * Values for the field are defined in the following table:
     *
     * <table>
     * <tr><th>Value</th><th>Algorithm</th></tr>
     * <tr><td>0</td><td>no encryption used</td></tr>
     * <tr><td>1</td><td>AES_128_CBC:<br></br>AES symmetric encryption as defined
     * by NIST. 128 bit keys, Cipher block chaining mode (CBC). For the first
     * block a 128-bit initialisation vector (IV) is used. For DCF files, the IV
     * is included in the OMADRMData as a prefix of the encrypted data. For
     * non-streamable PDCF files, the IV is included in the IV field of the
     * OMAAUHeader and the IVLength field in the OMAAUFormatBox MUST be set to
     * 16. Padding according to RFC 2630</td></tr>
     * <tr><td>2</td><td>AES_128_CTR:<br></br>AES symmetric encryption as defined
     * by NIST. 128 bit keys, Counter mode (CTR). The counter block has a length
     * of 128 bits. For DCF files, the initial counter value is included in the
     * OMADRMData as a prefix of the encrypted data. For non-streamable PDCF
     * files, the initial counter value is included in the IV field of the
     * OMAAUHeader  and the IVLength field in the OMAAUFormatBox MUST be set to
     * 16. For each cipherblock the counter is incremented by 1 (modulo 2128).
     * No padding.</td></tr>
    </table> *
     *
     * @return the encryption method
     */
    var encryptionMethod = 0
        private set

    /**
     * The padding scheme defines how the last block of ciphertext is padded.
     * Values of the padding scheme field are defined in the following table:
     *
     * <table>
     * <tr><th>Value</th><th>Padding scheme</th></tr>
     * <tr><td>0</td><td>No padding (e.g. when using NULL or CTR algorithm)</td></tr>
     * <tr><td>1</td><td>Padding according to RFC 2630</td></tr>
    </table> *
     *
     * @return the padding scheme
     */
    var paddingScheme = 0
        private set
    var plaintextLength: Long = 0
        private set
    lateinit var contentID: ByteArray
        private set
    lateinit var rightsIssuerURL: ByteArray
        private set
    private var textualHeaders: MutableMap<String, String>? = null

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        encryptionMethod = `in`.read()
        paddingScheme = `in`.read()
        plaintextLength = `in`.readBytes(8)
        val contentIDLength = `in`.readBytes(2).toInt()
        val rightsIssuerURLLength = `in`.readBytes(2).toInt()
        var textualHeadersLength = `in`.readBytes(2).toInt()
        contentID = ByteArray(contentIDLength)
        `in`.readBytes(contentID)
        rightsIssuerURL = ByteArray(rightsIssuerURLLength)
        `in`.readBytes(rightsIssuerURL)
        textualHeaders = HashMap<String, String>()
        var key: String
        var value: String
        while (textualHeadersLength > 0) {
            key = `in`.readTerminated(getLeft(`in`).toInt(), ':'.code).decodeToString()
            value = `in`.readTerminated(getLeft(`in`).toInt(), 0).decodeToString()
            textualHeaders!![key] = value
            textualHeadersLength -= key.length + value.length + 2
        }
        readChildren(`in`)
    }

    fun getTextualHeaders(): Map<String, String>? {
        return textualHeaders
    }
}
