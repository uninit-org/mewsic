package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec

import net.sourceforge.jaad.mp4.MP4InputStream

//defined in ISO 14496-15 as 'AVC Configuration Record'
class AVCSpecificBox : net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox("AVC Specific Box") {
    var configurationVersion = 0
        private set

    /**
     * The AVC profile code as defined in ISO/IEC 14496-10.
     *
     * @return the AVC profile
     */
    var profile = 0
        private set
    var level = 0
        private set

    /**
     * The length in bytes of the NALUnitLength field in an AVC video sample or
     * AVC parameter set sample of the associated stream. The value of this
     * field 1, 2, or 4 bytes.
     *
     * @return the NALUnitLength length in bytes
     */
    var lengthSize = 0
        private set

    /**
     * The profileCompatibility is a byte defined exactly the same as the byte
     * which occurs between the profileIDC and levelIDC in a sequence parameter
     * set (SPS), as defined in ISO/IEC 14496-10.
     *
     * @return the profile compatibility byte
     */
    var profileCompatibility: Byte = 0
        private set

    /**
     * The SPS NAL units, as specified in ISO/IEC 14496-10. SPSs shall occur in
     * order of ascending parameter set identifier with gaps being allowed.
     *
     * @return all SPS NAL units
     */
    var sequenceParameterSetNALUnits: Array<ByteArray?>
        private set

    /**
     * The PPS NAL units, as specified in ISO/IEC 14496-10. PPSs shall occur in
     * order of ascending parameter set identifier with gaps being allowed.
     *
     * @return all PPS NAL units
     */
    var pictureParameterSetNALUnits: Array<ByteArray?>
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        configurationVersion = `in`.read()
        profile = `in`.read()
        profileCompatibility = `in`.read() as Byte
        level = `in`.read()
        //6 bits reserved, 2 bits 'length size minus one'
        lengthSize = (`in`.read() and 3) + 1
        var len: Int
        //3 bits reserved, 5 bits number of sequence parameter sets
        val sequenceParameterSets: Int = `in`.read() and 31
        sequenceParameterSetNALUnits = arrayOfNulls(sequenceParameterSets)
        for (i in 0 until sequenceParameterSets) {
            len = `in`.readBytes(2) as Int
            sequenceParameterSetNALUnits[i] = ByteArray(len)
            `in`.readBytes(sequenceParameterSetNALUnits[i]!!)
        }
        val pictureParameterSets: Int = `in`.read()
        pictureParameterSetNALUnits = arrayOfNulls(pictureParameterSets)
        for (i in 0 until pictureParameterSets) {
            len = `in`.readBytes(2) as Int
            pictureParameterSetNALUnits[i] = ByteArray(len)
            `in`.readBytes(pictureParameterSetNALUnits[i]!!)
        }
    }
}
