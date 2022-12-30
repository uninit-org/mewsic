package net.sourceforge.jaad.mp4.boxes.impl.fd
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

class FilePartitionBox : FullBox("File Partition Box") {
    /**
     * The item ID references the item in the item location box that the file
     * partitioning applies to.
     *
     * @return the item ID
     */
    var itemID = 0
        private set

    /**
     * The packet payload size gives the target ALC/LCT or FLUTE packet payload
     * size of the partitioning algorithm. Note that UDP packet payloads are
     * larger, as they also contain ALC/LCT or FLUTE headers.
     *
     * @return the packet payload size
     */
    var packetPayloadSize = 0
        private set

    /**
     * The FEC encoding ID is subject to IANA registration (see RFC 3452). Note
     * that
     * - value zero corresponds to the "Compact No-Code FEC scheme" also known
     * as "Null-FEC" (RFC 3695);
     * - value one corresponds to the "MBMS FEC" (3GPP TS 26.346);
     * - for values in the range of 0 to 127, inclusive, the FEC scheme is
     * Fully-Specified, whereas for values in the range of 128 to 255,
     * inclusive, the FEC scheme is Under-Specified.
     *
     * @return the FEC encoding ID
     */
    var fECEncodingID = 0
        private set

    /**
     * The FEC instance ID provides a more specific identification of the FEC
     * encoder being used for an Under-Specified FEC scheme. This value should
     * be set to zero for Fully-Specified FEC schemes and shall be ignored when
     * parsing a file with an FEC encoding ID in the range of 0 to 127,
     * inclusive. The FEC instance ID is scoped by the FEC encoding ID. See RFC
     * 3452 for further details.
     *
     * @return the FEC instance ID
     */
    var fECInstanceID = 0
        private set

    /**
     * The maximum source block length gives the maximum number of source
     * symbols per source block.
     *
     * @return the maximum source block length
     */
    var maxSourceBlockLength = 0
        private set

    /**
     * The encoding symbol length gives the size (in bytes) of one encoding
     * symbol. All encoding symbols of one item have the same length, except the
     * last symbol which may be shorter.
     *
     * @return the encoding symbol length
     */
    var encodingSymbolLength = 0
        private set

    /**
     * The maximum number of encoding symbols  that can be generated for a
     * source block for those FEC schemes in which the maximum number of
     * encoding symbols is relevant, such as FEC encoding ID 129 defined in RFC
     * 3452. For those FEC schemes in which the maximum number of encoding
     * symbols is not relevant, the semantics of this field is unspecified.
     *
     * @return the maximum number of encoding symbols
     */
    var maxNumberOfEncodingSymbols = 0
        private set

    /**
     * The scheme specific info is a String of the scheme-specific object
     * transfer information (FEC-OTI-Scheme-Specific-Info). The definition of
     * the information depends on the EC encoding ID.
     *
     * @return the scheme specific info
     */
    var schemeSpecificInfo: String? = null
        private set

    /**
     * A block count indicates the number of consecutive source blocks with a
     * specified size.
     *
     * @return all block counts
     */
    lateinit var blockCounts: IntArray
        private set

    /**
     * A block size indicates the size of a block (in bytes). A block_size that
     * is not a multiple of the encoding symbol length indicates with Compact
     * No-Code FEC that the last source symbols includes padding that is not
     * stored in the item. With MBMS FEC (3GPP TS 26.346) the padding may extend
     * across multiple symbols but the size of padding should never be more than
     * the encoding symbol length.
     *
     * @return all block sizes
     */
    lateinit var blockSizes: LongArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        itemID = `in`.readBytes(2).toInt()
        packetPayloadSize = `in`.readBytes(2).toInt()
        `in`.skipBytes(1) //reserved
        fECEncodingID = `in`.read()
        fECInstanceID = `in`.readBytes(2).toInt()
        maxSourceBlockLength = `in`.readBytes(2).toInt()
        encodingSymbolLength = `in`.readBytes(2).toInt()
        maxNumberOfEncodingSymbols = `in`.readBytes(2).toInt()
        schemeSpecificInfo = net.sourceforge.jaad.mp4.boxes.impl.fd.Base64Decoder.decode(
            `in`.readTerminated(
                getLeft(`in`).toInt(),
                0
            )
        ).decodeToString()
        val entryCount = `in`.readBytes(2).toInt()
        blockCounts = IntArray(entryCount)
        blockSizes = LongArray(entryCount)
        for (i in 0 until entryCount) {
            blockCounts[i] = `in`.readBytes(2).toInt()
            blockSizes[i] = (`in`.readBytes(4).toInt()).toLong()
        }
    }
}
