package org.mewsic.jaad.aac

/**
 * All possible channel configurations for AAC.
 * @author in-somnia
 */
enum class ChannelConfiguration(
    /**
     * Returns the number of channels in this configuration.
     */
    val channelCount: Int,
    /**
     * Returns a short description of this configuration.
     * @return the channel configuration's description
     */
    val description: String
) {
    CHANNEL_CONFIG_UNSUPPORTED(-1, "invalid"), CHANNEL_CONFIG_NONE(0, "No channel"), CHANNEL_CONFIG_MONO(
        1,
        "Mono"
    ),
    CHANNEL_CONFIG_STEREO(2, "Stereo"), CHANNEL_CONFIG_STEREO_PLUS_CENTER(
        3,
        "Stereo+Center"
    ),
    CHANNEL_CONFIG_STEREO_PLUS_CENTER_PLUS_REAR_MONO(4, "Stereo+Center+Rear"), CHANNEL_CONFIG_FIVE(
        5,
        "Five channels"
    ),
    CHANNEL_CONFIG_FIVE_PLUS_ONE(6, "Five channels+LF"), CHANNEL_CONFIG_SEVEN_PLUS_ONE(8, "Seven channels+LF");

    /**
     * Returns a string representation of this channel configuration.
     * The method is identical to `getDescription()`.
     * @return the channel configuration's description
     */
    override fun toString(): String {
        return description
    }

    companion object {
        fun forInt(i: Int): ChannelConfiguration {
            val c: ChannelConfiguration = when (i) {
                0 -> CHANNEL_CONFIG_NONE
                1 -> CHANNEL_CONFIG_MONO
                2 -> CHANNEL_CONFIG_STEREO
                3 -> CHANNEL_CONFIG_STEREO_PLUS_CENTER
                4 -> CHANNEL_CONFIG_STEREO_PLUS_CENTER_PLUS_REAR_MONO
                5 -> CHANNEL_CONFIG_FIVE
                6 -> CHANNEL_CONFIG_FIVE_PLUS_ONE
                7, 8 -> CHANNEL_CONFIG_SEVEN_PLUS_ONE
                else -> CHANNEL_CONFIG_UNSUPPORTED
            }
            return c
        }
    }
}
