package net.sourceforge.jaad.aac
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.aac.SampleFrequency.Companion.forFrequency
import net.sourceforge.jaad.aac.syntax.BitStream
import net.sourceforge.jaad.aac.syntax.Constants
import net.sourceforge.jaad.aac.syntax.PCE

/**
 * DecoderConfig that must be passed to the
 * `Decoder` constructor. Typically it is created via one of the
 * static parsing methods.
 *
 * @author in-somnia
 */
class DecoderConfig private constructor() : Constants {
    private var profile: Profile?
    var extObjectType: Profile?
    private var sampleFrequency: SampleFrequency
    private var channelConfiguration: ChannelConfiguration
    var isSmallFrameUsed = false
    private var dependsOnCoreCoder = false
    private var coreCoderDelay = 0
    private var extensionFlag = false

    //=========== SBR =============
    //extension: SBR
    var isSBRPresent = false
        private set
    var isSBRDownSampled = false
        private set
    var isSBREnabled = true

    //extension: error resilience
    var isSectionDataResilienceUsed = false
        private set

    //=========== ER =============
    var isScalefactorResilienceUsed = false
        private set
    var isSpectralDataResilienceUsed = false
        private set

    init {
        profile = Profile.AAC_MAIN
        extObjectType = Profile.UNKNOWN
        sampleFrequency = SampleFrequency.SAMPLE_FREQUENCY_NONE
        channelConfiguration = ChannelConfiguration.CHANNEL_CONFIG_UNSUPPORTED
    }

    /* ========== gets/sets ========== */
    fun getChannelConfiguration(): ChannelConfiguration {
        return channelConfiguration
    }

    fun setChannelConfiguration(channelConfiguration: ChannelConfiguration) {
        this.channelConfiguration = channelConfiguration
    }

    fun getCoreCoderDelay(): Int {
        return coreCoderDelay
    }

    fun setCoreCoderDelay(coreCoderDelay: Int) {
        this.coreCoderDelay = coreCoderDelay
    }

    fun isDependsOnCoreCoder(): Boolean {
        return dependsOnCoreCoder
    }

    fun setDependsOnCoreCoder(dependsOnCoreCoder: Boolean) {
        this.dependsOnCoreCoder = dependsOnCoreCoder
    }

    val frameLength: Int
        get() = if (isSmallFrameUsed) Constants.WINDOW_SMALL_LEN_LONG else Constants.WINDOW_LEN_LONG

    fun getProfile(): Profile? {
        return profile
    }

    fun setProfile(profile: Profile?) {
        this.profile = profile
    }

    fun getSampleFrequency(): SampleFrequency {
        return sampleFrequency
    }

    fun setSampleFrequency(sampleFrequency: SampleFrequency) {
        this.sampleFrequency = sampleFrequency
    }

    companion object {
        /* ======== static builder ========= */
        /**
         * Parses the input arrays as a DecoderSpecificInfo, as used in MP4
         * containers.
         *
         * @return a DecoderConfig
         */
        fun parseMP4DecoderSpecificInfo(data: ByteArray): DecoderConfig {
            val `in` = BitStream(data)
            val config = DecoderConfig()
            return try {
                config.profile = readProfile(`in`)
                var sf = `in`.readBits(4)
                if (sf == 0xF) config.sampleFrequency =
                    forFrequency(`in`.readBits(24)) else config.sampleFrequency = SampleFrequency.forInt(sf)
                config.channelConfiguration = ChannelConfiguration.forInt(`in`.readBits(4))
                when (config.profile) {
                    Profile.AAC_SBR -> {
                        config.extObjectType = config.profile
                        config.isSBRPresent = true
                        sf = `in`.readBits(4)
                        //TODO: 24 bits already read; read again?
                        //if(sf==0xF) config.sampleFrequency = SampleFrequency.forFrequency(in.readBits(24));
                        //if sample frequencies are the same: downsample SBR
                        config.isSBRDownSampled = config.sampleFrequency.index == sf
                        config.sampleFrequency = SampleFrequency.forInt(sf)
                        config.profile = readProfile(`in`)
                    }

                    Profile.AAC_MAIN, Profile.AAC_LC, Profile.AAC_SSR, Profile.AAC_LTP, Profile.ER_AAC_LC, Profile.ER_AAC_LTP, Profile.ER_AAC_LD -> {
                        //ga-specific info:
                        config.isSmallFrameUsed = `in`.readBool()
                        if (config.isSmallFrameUsed) throw AACException("config uses 960-sample frames, not yet supported") //TODO: are 960-frames working yet?
                        config.dependsOnCoreCoder = `in`.readBool()
                        if (config.dependsOnCoreCoder) config.coreCoderDelay =
                            `in`.readBits(14) else config.coreCoderDelay = 0
                        config.extensionFlag = `in`.readBool()
                        if (config.extensionFlag) {
                            if (config.profile!!.isErrorResilientProfile) {
                                config.isSectionDataResilienceUsed = `in`.readBool()
                                config.isScalefactorResilienceUsed = `in`.readBool()
                                config.isSpectralDataResilienceUsed = `in`.readBool()
                            }
                            //extensionFlag3
                            `in`.skipBit()
                        }
                        if (config.channelConfiguration === ChannelConfiguration.CHANNEL_CONFIG_NONE) {
                            //TODO: is this working correct? -> ISO 14496-3 part 1: 1.A.4.3
                            `in`.skipBits(3) //PCE
                            val pce = PCE()
                            pce.decode(`in`)
                            config.profile = pce.profile
                            config.sampleFrequency = pce.sampleFrequency
                            config.channelConfiguration = ChannelConfiguration.forInt(pce.channelCount)
                        }
                        if (`in`.bitsLeft > 10) readSyncExtension(`in`, config)
                    }

                    else -> throw AACException("profile not supported: " + config.profile!!.index)
                }
                config
            } finally {
                `in`.destroy()
            }
        }

        private fun readProfile(`in`: BitStream): Profile? {
            var i = `in`.readBits(5)
            if (i == 31) i = 32 + `in`.readBits(6)
            return Profile.forInt(i)
        }

        private fun readSyncExtension(`in`: BitStream, config: DecoderConfig) {
            val type = `in`.readBits(11)
            when (type) {
                0x2B7 -> {
                    val profile = Profile.forInt(`in`.readBits(5))
                    if (profile == Profile.AAC_SBR) {
                        config.isSBRPresent = `in`.readBool()
                        if (config.isSBRPresent) {
                            config.profile = profile
                            val tmp = `in`.readBits(4)
                            if (tmp == config.sampleFrequency.index) config.isSBRDownSampled = true
                            if (tmp == 15) {
                                throw AACException("sample rate specified explicitly, not supported yet!")
                                //tmp = in.readBits(24);
                            }
                        }
                    }
                }
            }
        }
    }
}
