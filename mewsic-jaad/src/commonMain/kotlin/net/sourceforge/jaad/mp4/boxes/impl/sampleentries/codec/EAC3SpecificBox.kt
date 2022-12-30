package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * This box contains parameters for Extended AC-3 decoders. For more information
 * see the AC-3 specification "`ETSI TS 102 366 V1.2.1 (2008-08)`" at
 * [](http://www.etsi.org/deliver/etsi_ts/102300_102399/102366/01.02.01_60/ts_102366v010201p.pdf>
  http://www.etsi.org/deliver/etsi_ts/102300_102399/102366/01.02.01_60/ts_102366v010201p.pdf</a>.
  
  @author in-somnia
) */
class EAC3SpecificBox : net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox("EAC-3 Specific Box") {
    /**
     * This value indicates the data rate of the Enhanced AC-3 bitstream in
     * kbit/s. If the Enhanced AC-3 stream is variable bit rate, then this value
     * indicates the maximum data rate of the stream.
     *
     * @return the data rate
     */
    var dataRate = 0
        private set

    /**
     * This field indicates the number of independent substreams that are
     * present in the Enhanced AC-3 bitstream.
     *
     * @return the number of independent substreams
     */
    var independentSubstreamCount = 0
        private set

    /**
     * This field has the same meaning and is set to the same value as the fscod
     * field in the independent substream.
     *
     * @return the 'fscod' values for all independent substreams
     */
    val fscods: IntArray

    /**
     * This field has the same meaning and is set to the same value as the bsid
     * field in the independent substream.
     *
     * @return the 'bsid' values for all independent substreams
     */
    val bsids: IntArray

    /**
     * This field has the same meaning and is set to the same value as the bsmod
     * field in the independent substream. If the bsmod field is not present in
     * the independent substream, this field shall be set to 0.
     *
     * @return the 'bsmod' values for all independent substreams
     */
    val bsmods: IntArray

    /**
     * This field has the same meaning and is set to the same value as the acmod
     * field in the independent substream.
     *
     * @return the 'acmod' values for all independent substreams
     */
    val acmods: IntArray

    /**
     * This field indicates the number of dependent substreams that are
     * associated with an independent substream.
     *
     * @return the number of dependent substreams for all independent substreams
     */
    val dependentSubstreamCount: IntArray

    /**
     * If there are one or more dependent substreams associated with an
     * independent substream, this bit field is used to identify channel
     * locations beyond those identified using the 'acmod' field that are
     * present in the bitstream. The lowest 9 bits of the returned integer are
     * flags indicating if a channel location is present. The flags are used
     * according to the following table, where index 0 is the most significant
     * bit of all 9 used bits:
     * <table>
     * <tr><th>Bit</th><th>Location</th></tr>
     * <tr><td>0</td><td>Lc/Rc pair</td></tr>
     * <tr><td>1</td><td>Lrs/Rrs pair</td></tr>
     * <tr><td>2</td><td>Cs</td></tr>
     * <tr><td>3</td><td>Ts</td></tr>
     * <tr><td>4</td><td>Lsd/Rsd pair</td></tr>
     * <tr><td>5</td><td>Lw/Rw pair</td></tr>
     * <tr><td>6</td><td>Lvh/Rvh pair </td></tr>
     * <tr><td>7</td><td>Cvh</td></tr>
     * <tr><td>8</td><td>LFE2</td></tr>
    </table> *
     *
     * @return the dependent substream locations for all independent substreams
     */
    val dependentSubstreamLocation: IntArray

    /**
     * This field has the same meaning and is set to the same value as the lfeon
     * field in the independent substream.
     *
     * @return the 'lfeon' values for all independent substreams
     */
    val lfeons: BooleanArray
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        var l: Long = `in`.readBytes(2)
        //13 bits dataRate
        dataRate = (l shr 3 and 0x1FFFL).toInt()
        //3 bits number of independent substreams
        independentSubstreamCount = (l and 0x7L).toInt()
        for (i in 0 until independentSubstreamCount) {
            l = `in`.readBytes(3)
            //2 bits fscod
            fscods[i] = (l shr 22 and 0x3L).toInt()
            //5 bits bsid
            bsids[i] = (l shr 17 and 0x1FL).toInt()
            //5 bits bsmod
            bsmods[i] = (l shr 12 and 0x1FL).toInt()
            //3 bits acmod
            acmods[i] = (l shr 9 and 0x7L).toInt()
            //3 bits reserved
            //1 bit lfeon
            lfeons[i] = l shr 5 and 0x1L == 1L
            //4 bits number of dependent substreams
            dependentSubstreamCount[i] = (l shr 1 and 0xFL).toInt()
            if (dependentSubstreamCount[i] > 0) {
                //9 bits dependent substream location
                l = l shl 8 or `in`.read().toLong()
                dependentSubstreamLocation[i] = (l and 0x1FFL).toInt()
            }
            //else: 1 bit reserved
        }
    }
}
