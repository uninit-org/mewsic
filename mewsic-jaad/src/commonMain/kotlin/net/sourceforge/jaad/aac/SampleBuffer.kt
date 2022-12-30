package net.sourceforge.jaad.aac
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
/**
 * The SampleBuffer holds the decoded AAC frame. It contains the raw PCM data
 * and its format.
 * @author in-somnia
 */
class SampleBuffer {
    /**
     * Returns the data's sample rate.
     * @return the sample rate
     */
    var sampleRate = 0
        private set

    /**
     * Returns the number of channels stored in the data buffer.
     * @return the number of channels
     */
    var channels = 0
        private set

    /**
     * Returns the number of bits per sample. Usually this is 16, meaning a
     * sample is stored in two bytes.
     * @return the number of bits per sample
     */
    var bitsPerSample = 0
        private set

    /**
     * Returns the length of the current frame in seconds.
     * length = samplesPerChannel / sampleRate
     * @return the length in seconds
     */
    var length = 0.0
        private set

    /**
     * Returns the bitrate of the decoded PCM data.
     * `bitrate = (samplesPerChannel * bitsPerSample) / length`
     * @return the bitrate
     */
    var bitrate = 0.0
        private set

    /**
     * Returns the AAC bitrate of the current frame.
     * @return the AAC bitrate
     */
    var encodedBitrate = 0.0
        private set

    /**
     * Returns the buffer's PCM data.
     * @return the audio data
     */
    lateinit var data: ByteArray
        private set
    private var bigEndian = true

    init {
        data = ByteArray(0)
    }

    /**
     * Indicates the endianness for the data.
     *
     * @return true if the data is in big endian, false if it is in little endian
     */
    fun isBigEndian(): Boolean {
        return bigEndian
    }

    /**
     * Sets the endianness for the data.
     *
     * @param bigEndian if true the data will be in big endian, else in little
     * endian
     */
    fun setBigEndian(bigEndian: Boolean) {
        if (bigEndian != this.bigEndian) {
            var tmp: Byte
            var i = 0
            while (i < data.size) {
                tmp = data[i]
                data[i] = data[i + 1]
                data[i + 1] = tmp
                i += 2
            }
            this.bigEndian = bigEndian
        }
    }

    fun setData(data: ByteArray, sampleRate: Int, channels: Int, bitsPerSample: Int, bitsRead: Int) {
        this.data = data
        this.sampleRate = sampleRate
        this.channels = channels
        this.bitsPerSample = bitsPerSample
        if (sampleRate == 0) {
            length = 0.0
            bitrate = 0.0
            encodedBitrate = 0.0
        } else {
            val bytesPerSample = bitsPerSample / 8 //usually 2
            val samplesPerChannel = data.size / (bytesPerSample * channels) //=1024
            length = samplesPerChannel.toDouble() / sampleRate.toDouble()
            bitrate = (samplesPerChannel * bitsPerSample * channels).toDouble() / length
            encodedBitrate = bitsRead.toDouble() / length
        }
    }
}
