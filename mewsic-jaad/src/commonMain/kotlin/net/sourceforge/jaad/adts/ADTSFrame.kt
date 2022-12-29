package net.sourceforge.jaad.adts

import net.sourceforge.jaad.aac.ChannelConfiguration
import net.sourceforge.jaad.aac.SampleFrequency.Companion.forInt
import org.mewsic.commons.streams.DataInputStream
internal class ADTSFrame(`in`: DataInputStream) {
    //fixed
    private var id = false
    private var protectionAbsent = false
    private var privateBit = false
    private var copy = false
    private var home = false
    private var layer = 0
    private var profile = 0
    private var sampleFrequency = 0
    private var channelConfiguration = 0

    //variable
    private var copyrightIDBit = false
    private var copyrightIDStart = false
    private var frameLength = 0
    private var adtsBufferFullness = 0
    private var rawDataBlockCount = 0

    //error check
    private var rawDataBlockPosition: IntArray = IntArray(0)
    private var crcCheck = 0

    //decoder specific info
    private var info: ByteArray? = null

    init {
        readHeader(`in`)
        if (!protectionAbsent) crcCheck = `in`.readUShort().toInt()
        if (rawDataBlockCount == 0) {
            //raw_data_block();
        } else {
            var i: Int
            //header error check
            if (!protectionAbsent) {
                rawDataBlockPosition = IntArray(rawDataBlockCount)
                i = 0
                while (i < rawDataBlockCount) {
                    rawDataBlockPosition[i] = `in`.readUShort().toInt()
                    i++
                }
                crcCheck = `in`.readUShort().toInt()
            }
            //raw data blocks
            i = 0
            while (i < rawDataBlockCount) {

                //raw_data_block();
                if (!protectionAbsent) crcCheck = `in`.readUShort().toInt()
                i++
            }
        }
    }

    @Throws(Exception::class)
    private fun readHeader(`in`: DataInputStream) {
        //fixed header:
        //1 bit ID, 2 bits layer, 1 bit protection absent
        var i: Int = `in`.read().toInt()
        id = i shr 3 and 0x1 == 1
        layer = i shr 1 and 0x3
        protectionAbsent = i and 0x1 == 1

        //2 bits profile, 4 bits sample frequency, 1 bit private bit
        i = `in`.read().toInt()
        profile = (i shr 6 and 0x3) + 1
        sampleFrequency = i shr 2 and 0xF
        privateBit = i shr 1 and 0x1 == 1

        //3 bits channel configuration, 1 bit copy, 1 bit home
        i = i shl 8 or `in`.read().toInt()
        channelConfiguration = i shr 6 and 0x7
        copy = i shr 5 and 0x1 == 1
        home = i shr 4 and 0x1 == 1
        //int emphasis = in.readBits(2);

        //variable header:
        //1 bit copyrightIDBit, 1 bit copyrightIDStart, 13 bits frame length,
        //11 bits adtsBufferFullness, 2 bits rawDataBlockCount
        copyrightIDBit = i shr 3 and 0x1 == 1
        copyrightIDStart = i shr 2 and 0x1 == 1
        i = i shl 16 or `in`.readUShort().toInt()
        frameLength = i shr 5 and 0x1FFF
        i = i shl 8 or `in`.read().toInt()
        adtsBufferFullness = i shr 2 and 0x7FF
        rawDataBlockCount = i and 0x3
    }

    fun getFrameLength(): Int {
        return frameLength - if (protectionAbsent) 7 else 9
    }

    fun createDecoderSpecificInfo(): ByteArray {
        if (info == null) {
            //5 bits profile, 4 bits sample frequency, 4 bits channel configuration
            info = ByteArray(2)
            info!![0] = (profile shl 3).toByte()
            info!![0] = (info!![0].toInt() or (sampleFrequency shr 1 and 0x7)).toByte()
            info!![1] = (sampleFrequency and 0x1 shl 7).toByte()
            info!![1] = (info!![1].toInt() or (channelConfiguration shl 3)).toByte()
            /*1 bit frame length flag, 1 bit depends on core coder,
			1 bit extension flag (all three currently 0)*/
        }
        return info as ByteArray
    }

    fun getSampleFrequency(): Int {
        return forInt(sampleFrequency).frequency
    }

    val channelCount: Int
        get() = ChannelConfiguration.forInt(channelConfiguration).channelCount
}
