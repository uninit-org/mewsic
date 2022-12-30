package net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * This box contains parameters for AC-3 decoders. For more information see the
 * AC-3 specification "`ETSI TS 102 366 V1.2.1 (2008-08)`" at
 * [](http://www.etsi.org/deliver/etsi_ts/102300_102399/102366/01.02.01_60/ts_102366v010201p.pdf>
  http://www.etsi.org/deliver/etsi_ts/102300_102399/102366/01.02.01_60/ts_102366v010201p.pdf</a>.
  
  @author in-somnia
) */
class AC3SpecificBox : net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox("AC-3 Specific Box") {
    /**
     * This field has the same meaning and is set to the same value as the fscod
     * field in the AC-3 bitstream.
     *
     * @return the value of the 'fscod' field
     */
    var fscod = 0
        private set

    /**
     * This field has the same meaning and is set to the same value as the bsid
     * field in the AC-3 bitstream.
     *
     * @return the value of the 'bsid' field
     */
    var bsid = 0
        private set

    /**
     * This field has the same meaning and is set to the same value as the bsmod
     * field in the AC-3 bitstream.
     *
     * @return the value of the 'acmod' field
     */
    var bsmod = 0
        private set

    /**
     * This field has the same meaning and is set to the same value as the acmod
     * field in the AC-3 bitstream.
     *
     * @return the value of the 'acmod' field
     */
    var acmod = 0
        private set

    /**
     * This field indicates the data rate of the AC-3 bitstream in kbit/s, as
     * shown in the following table:
     * <table>
     * <tr><th>bit rate code</th><th>bit rate (kbit/s)</th></tr>
     * <tr><td>0</td><td>32</td></tr>
     * <tr><td>1</td><td>40</td></tr>
     * <tr><td>2</td><td>48</td></tr>
     * <tr><td>3</td><td>56</td></tr>
     * <tr><td>4</td><td>64</td></tr>
     * <tr><td>5</td><td>80</td></tr>
     * <tr><td>6</td><td>96</td></tr>
     * <tr><td>7</td><td>112</td></tr>
     * <tr><td>8</td><td>128</td></tr>
     * <tr><td>9</td><td>160</td></tr>
     * <tr><td>10</td><td>192</td></tr>
     * <tr><td>11</td><td>224</td></tr>
     * <tr><td>12</td><td>256</td></tr>
     * <tr><td>13</td><td>320</td></tr>
     * <tr><td>14</td><td>384</td></tr>
     * <tr><td>15</td><td>448</td></tr>
     * <tr><td>16</td><td>512</td></tr>
     * <tr><td>17</td><td>576</td></tr>
     * <tr><td>18</td><td>640</td></tr>
    </table> *
     *
     * @return the bit rate code
     */
    var bitRateCode = 0
        private set

    /**
     * This field has the same meaning and is set to the same value as the lfeon
     * field in the AC-3 bitstream.
     *
     * @return the value of the 'lfeon' field
     */
    var isLfeon = false
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        val l: Long = `in`.readBytes(3)

        //2 bits fscod
        fscod = (l shr 22 and 0x3L).toInt()
        //5 bits bsid
        bsid = (l shr 17 and 0x1FL).toInt()
        //3 bits bsmod
        bsmod = (l shr 14 and 0x7L).toInt()
        //3 bits acmod
        acmod = (l shr 11 and 0x7L).toInt()
        //1 bit lfeon
        isLfeon = l shr 10 and 0x1L == 1L
        //5 bits bitRateCode
        bitRateCode = (l shr 5 and 0x1FL).toInt()
        //5 bits reserved
    }
}
