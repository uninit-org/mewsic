package net.sourceforge.jaad.mp4.od
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The ESDescriptor conveys all information related to a particular elementary
 * stream and has three major parts:
 *
 * The first part consists of the ES_ID which is a unique reference to the
 * elementary stream within its name scope, a mechanism to group elementary
 * streams within this Descriptor and an optional URL String.
 *
 * The second part is a set of optional extension descriptors that support the
 * inclusion of future extensions as well as the transport of private data in a
 * backward compatible way.
 *
 * The third part consists of the DecoderConfigDescriptor, SLConfigDescriptor,
 * IPIDescriptor and QoSDescriptor which convey the parameters and requirements
 * of the elementary stream.
 *
 * @author in-somnia
 */
class ESDescriptor : net.sourceforge.jaad.mp4.od.Descriptor() {
    /**
     * The ES_ID provides a unique label for each elementary stream within its
     * name scope. The value should be within 0 and 65535 exclusively. The
     * values 0 and 65535 are reserved.
     *
     * @return the elementary stream's ID
     */
    var eS_ID = 0
        private set

    /**
     * The stream priority indicates a relative measure for the priority of this
     * elementary stream. An elementary stream with a higher priority is more
     * important than one with a lower priority. The absolute values are not
     * normatively defined.
     *
     * @return the stream priority
     */
    var streamPriority = 0
        private set

    /**
     * The `dependingOnES_ID` is the ES_ID of another elementary
     * stream on which this elementary stream depends. The stream with the
     * `dependingOnES_ID` shall also be associated to this
     * Descriptor. If no value is present (if `hasStreamDependency()`
     * returns false) this method returns -1.
     *
     * @return the dependingOnES_ID value, or -1 if none is present
     */
    var dependingOnES_ID = 0
        private set
    private var streamDependency = false

    /**
     * A flag that indicates the presence of a URL.
     *
     * @return true if a URL is present
     */
    var isURLPresent = false
        private set
    private val ocrPresent = false

    /**
     * A URL String that shall point to the location of an SL-packetized stream
     * by name. The parameters of the SL-packetized stream that is retrieved
     * from the URL are fully specified in this ESDescriptor.
     * If no URL is present (if `isURLPresent()` returns false) this
     * method returns null.
     *
     * @return a URL String or null if none is present
     */
    var uRL: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        eS_ID = `in`.readBytes(2).toInt()

        //1 bit stream dependence flag, 1 it url flag, 1 reserved, 5 bits stream priority
        val flags: Int = `in`.read()
        streamDependency = flags shr 7 and 1 == 1
        isURLPresent = flags shr 6 and 1 == 1
        streamPriority = flags and 31
        dependingOnES_ID = if (streamDependency) `in`.readBytes(2).toInt() else -1
        if (isURLPresent) {
            val len: Int = `in`.read()
            uRL = `in`.readString(len)
        }
        readChildren(`in`)
    }

    /**
     * Indicates if an ID of another stream is present, on which this stream
     * depends.
     *
     * @return true if the dependingOnES_ID is present
     */
    fun hasStreamDependency(): Boolean {
        return streamDependency
    }
}
