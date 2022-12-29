package net.sourceforge.jaad.spi.javasound

import net.sourceforge.jaad.aac.syntax.BitStream
import net.sourceforge.jaad.adts.ADTSDemultiplexer

class AACAudioFileReader : AudioFileReader() {
    @Throws(UnsupportedAudioFileException::class, java.io.IOException::class)
    override fun getAudioFileFormat(`in`: java.io.InputStream): AudioFileFormat {
        var `in`: java.io.InputStream = `in`
        return try {
            if (!`in`.markSupported()) `in` = java.io.BufferedInputStream(`in`)
            `in`.mark(4)
            getAudioFileFormat(`in`, AudioSystem.NOT_SPECIFIED)
        } finally {
            `in`.reset()
        }
    }

    @Throws(UnsupportedAudioFileException::class, java.io.IOException::class)
    override fun getAudioFileFormat(url: java.net.URL): AudioFileFormat {
        val `in`: java.io.InputStream = url.openStream()
        return try {
            getAudioFileFormat(`in`)
        } finally {
            if (`in` != null) `in`.close()
        }
    }

    @Throws(UnsupportedAudioFileException::class, java.io.IOException::class)
    override fun getAudioFileFormat(file: java.io.File): AudioFileFormat {
        var `in`: java.io.InputStream? = null
        return try {
            `in` = java.io.BufferedInputStream(java.io.FileInputStream(file))
            `in`.mark(1000)
            val aff: AudioFileFormat = getAudioFileFormat(`in`, file.length().toInt())
            `in`.reset()
            aff
        } finally {
            if (`in` != null) `in`.close()
        }
    }

    @Throws(UnsupportedAudioFileException::class, java.io.IOException::class)
    private fun getAudioFileFormat(`in`: java.io.InputStream?, mediaLength: Int): AudioFileFormat {
        val head = ByteArray(12)
        `in`.read(head)
        var canHandle = false
        canHandle = if (kotlin.String(
                head,
                4,
                4
            ) == "ftyp"
        ) true else if (head[0] == 'R'.code.toByte() && head[1] == 'I'.code.toByte() && head[2] == 'F'.code.toByte() && head[3] == 'F'.code.toByte() && head[8] == 'W'.code.toByte() && head[9] == 'A'.code.toByte() && head[10] == 'V'.code.toByte() && head[11] == 'E'.code.toByte()) {
            false //RIFF/WAV stream found
        } else if (head[0] == '.'.code.toByte() && head[1] == 's'.code.toByte() && head[2] == 'n'.code.toByte() && head[3] == 'd'.code.toByte()) {
            false //AU stream found
        } else if (head[0] == 'F'.code.toByte() && head[1] == 'O'.code.toByte() && head[2] == 'R'.code.toByte() && head[3] == 'M'.code.toByte() && head[8] == 'A'.code.toByte() && head[9] == 'I'.code.toByte() && head[10] == 'F'.code.toByte() && head[11] == 'F'.code.toByte()) {
            false //AIFF stream found
        } else if ((head[0] == 'M'.code.toByte()) or (head[0] == 'm'.code.toByte()) && (head[1] == 'A'.code.toByte()) or (head[1] == 'a'.code.toByte()) && (head[2] == 'C'.code.toByte()) or (head[2] == 'c'.code.toByte())) {
            false //APE stream found
        } else if ((head[0] == 'F'.code.toByte()) or (head[0] == 'f'.code.toByte()) && (head[1] == 'L'.code.toByte()) or (head[1] == 'l'.code.toByte()) && (head[2] == 'A'.code.toByte()) or (head[2] == 'a'.code.toByte()) && (head[3] == 'C'.code.toByte()) or (head[3] == 'c'.code.toByte())) {
            false //FLAC stream found
        } else if ((head[0] == 'I'.code.toByte()) or (head[0] == 'i'.code.toByte()) && (head[1] == 'C'.code.toByte()) or (head[1] == 'c'.code.toByte()) && (head[2] == 'Y'.code.toByte()) or (head[2] == 'y'.code.toByte())) {
            false //Shoutcast / ICE stream ?
        } else if ((head[0] == 'O'.code.toByte()) or (head[0] == 'o'.code.toByte()) && (head[1] == 'G'.code.toByte()) or (head[1] == 'g'.code.toByte()) && (head[2] == 'G'.code.toByte()) or (head[2] == 'g'.code.toByte())) {
            false //Ogg stream ?
        } else {
            val bit = BitStream(head)
            try {
                val adts = ADTSDemultiplexer(`in`)
                true
            } catch (e: java.lang.Exception) {
                false
            }
        }
        return if (canHandle) {
            val format: javax.sound.sampled.AudioFormat = javax.sound.sampled.AudioFormat(
                AAC_ENCODING,
                AudioSystem.NOT_SPECIFIED.toFloat(),
                AudioSystem.NOT_SPECIFIED,
                mediaLength,
                AudioSystem.NOT_SPECIFIED,
                AudioSystem.NOT_SPECIFIED.toFloat(),
                true
            )
            AudioFileFormat(AAC, format, AudioSystem.NOT_SPECIFIED)
        } else throw UnsupportedAudioFileException()
    }

    //================================================
    @Throws(UnsupportedAudioFileException::class, java.io.IOException::class)
    override fun getAudioInputStream(`in`: java.io.InputStream): AudioInputStream {
        var `in`: java.io.InputStream = `in`
        return try {
            if (!`in`.markSupported()) `in` = java.io.BufferedInputStream(`in`)
            `in`.mark(1000)
            val aff: AudioFileFormat = getAudioFileFormat(`in`, AudioSystem.NOT_SPECIFIED)
            `in`.reset()
            net.sourceforge.jaad.spi.javasound.MP4AudioInputStream(`in`, aff.getFormat(), aff.getFrameLength().toLong())
        } catch (e: UnsupportedAudioFileException) {
            `in`.reset()
            throw e
        } catch (e: java.io.IOException) {
            if (e.message == net.sourceforge.jaad.spi.javasound.MP4AudioInputStream.Companion.ERROR_MESSAGE_AAC_TRACK_NOT_FOUND) {
                throw UnsupportedAudioFileException(net.sourceforge.jaad.spi.javasound.MP4AudioInputStream.Companion.ERROR_MESSAGE_AAC_TRACK_NOT_FOUND)
            } else {
                `in`.reset()
                throw e
            }
        }
    }

    @Throws(UnsupportedAudioFileException::class, java.io.IOException::class)
    override fun getAudioInputStream(url: java.net.URL): AudioInputStream {
        val `in`: java.io.InputStream = url.openStream()
        return try {
            getAudioInputStream(`in`)
        } catch (e: UnsupportedAudioFileException) {
            if (`in` != null) `in`.close()
            throw e
        } catch (e: java.io.IOException) {
            if (`in` != null) `in`.close()
            throw e
        }
    }

    @Throws(UnsupportedAudioFileException::class, java.io.IOException::class)
    override fun getAudioInputStream(file: java.io.File): AudioInputStream {
        val `in`: java.io.InputStream = java.io.FileInputStream(file)
        return try {
            getAudioInputStream(`in`)
        } catch (e: UnsupportedAudioFileException) {
            if (`in` != null) `in`.close()
            throw e
        } catch (e: java.io.IOException) {
            if (`in` != null) `in`.close()
            throw e
        }
    }

    companion object {
        val AAC: javax.sound.sampled.AudioFileFormat.Type = javax.sound.sampled.AudioFileFormat.Type("AAC", "aac")
        val MP4: javax.sound.sampled.AudioFileFormat.Type = javax.sound.sampled.AudioFileFormat.Type("MP4", "mp4")
        private val AAC_ENCODING: javax.sound.sampled.AudioFormat.Encoding =
            javax.sound.sampled.AudioFormat.Encoding("AAC")
    }
}
