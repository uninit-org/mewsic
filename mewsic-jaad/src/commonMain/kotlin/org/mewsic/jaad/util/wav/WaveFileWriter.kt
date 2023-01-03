package org.mewsic.jaad.util.wav

import org.mewsic.commons.binary.SeekableBinaryWriter
import org.mewsic.commons.lang.Log
import kotlin.jvm.JvmOverloads

class WaveFileWriter(
    private val out: SeekableBinaryWriter,
    private val sampleRate: Int,
    private val channels: Int,
    private val bitsPerSample: Int
) {
    private var bytesWritten = 0

    init {
        out.write(ByteArray(HEADER_LENGTH)) //space for the header
    }

    @JvmOverloads
    @Throws(Exception::class)
    fun write(data: ByteArray, off: Int = 0, len: Int? = null) {
        //convert to little endian
        var tmp: Byte
        var i = off
        while (i < off + data.size) {
            tmp = data[i + 1]
            data[i + 1] = data[i]
            data[i] = tmp
            i += 2
        }
        if (len != null) {
            out.write(data, off, len)
        } else {
            out.write(data, off, data.size)
        }
        bytesWritten += data.size
    }

    @JvmOverloads
    @Throws(Exception::class)
    fun write(data: ShortArray, off: Int = 0, len: Int? = null) {
        for (i in off until off + data.size) {
            out.write((data[i].toInt() and BYTE_MASK).toByte())
            out.write((data[i].toInt() shr 8 and BYTE_MASK).toByte())
            bytesWritten += 2
        }
    }

    @Throws(Exception::class)
    fun close() {
        writeWaveHeader()
        Log.info("WAV file written: $bytesWritten bytes")
    }

    @Throws(Exception::class)
    private fun writeWaveHeader() {
        out.seek(0)
        val bytesPerSec = (bitsPerSample + 7) / 8
        out.writeInt(RIFF) //wave label
        out.writeInt((bytesWritten + 36)) //length in bytes without header
        out.writeLong(WAVE_FMT)
        out.writeInt((16)) //length of pcm format declaration area
        out.writeShort((1.toShort())) //is PCM
        out.writeShort((channels.toShort())) //number of channels
        out.writeInt((sampleRate)) //sample rate
        out.writeInt((sampleRate * channels * bytesPerSec)) //bytes per second
        out.writeShort(((channels * bytesPerSec).toShort())) //bytes per sample time
        out.writeShort((bitsPerSample.toShort())) //bits per sample
        out.writeInt(DATA) //data section label
        out.writeInt((bytesWritten)) //length of raw pcm data in bytes
    }

    companion object {
        private const val HEADER_LENGTH = 44
        private const val RIFF = 1380533830 //'RIFF'
        private const val WAVE_FMT = 6287401410857104416L //'WAVEfmt '
        private const val DATA = 1684108385 //'data'
        private const val BYTE_MASK = 0xFF
    }
}
