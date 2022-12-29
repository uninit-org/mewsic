package net.sourceforge.jaad.mp4.od

import net.sourceforge.jaad.aac.SampleFrequency.Companion.forInt
import net.sourceforge.jaad.aac.Decoder.decodeFrame
import net.sourceforge.jaad.aac.SampleBuffer.sampleRate
import net.sourceforge.jaad.aac.SampleBuffer.bitsPerSample
import net.sourceforge.jaad.aac.SampleBuffer.channels
import net.sourceforge.jaad.aac.SampleBuffer.data
import net.sourceforge.jaad.aac.SampleFrequency
import net.sourceforge.jaad.aac.ChannelConfiguration
import net.sourceforge.jaad.mp4.api.Track
import net.sourceforge.jaad.mp4.api.Protection
import net.sourceforge.jaad.mp4.api.AudioTrack
import javax.imageio.ImageIO
import net.sourceforge.jaad.mp4.api.AudioTrack.AudioCodec
import javax.sound.sampled.spi.AudioFileReader
import javax.sound.sampled.UnsupportedAudioFileException
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.AudioInputStream
import net.sourceforge.jaad.aac.SampleBuffer

/**
 * This package contains `ObjectDescriptor` implementations as
 * defined in ISO/IEC 14496-1: Systems and ISO/IEC 14496-14: MP4.
 * In the MP4 file format object descriptors can be present inside an
 * `ESDBox`, which is a child of the `SampleDescription`
 * box, or in an `ObjectDescriptorBox`.
 */
