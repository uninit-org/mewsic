package org.mewsic.jaad.aac

import kotlin.jvm.JvmStatic

/**
 * Different AAC profiles.
 * The function `Decoder.canDecode` specifies if the decoder can
 * handle a given format.
 * More precisely, the ISO standard calls these 'object types'.
 * @author in-somnia
 */
enum class Profile(
    /**
     * Returns this profile's index between 1 and 23 or -1 if this is the
     * `UNKNOWN` instance.
     * @return the profile's index
     */
    val index: Int,
    /**
     * Returns a short description of this profile.
     * @return the profile's description
     */
    val description: String,
    /**
     * Returns a boolean, indicating if this profile can be decoded by the
     * `Decoder`.
     * @see Decoder.canDecode
     * @return true if the profile is supported
     */
    val isDecodingSupported: Boolean
) {
    UNKNOWN(-1, "unknown", false), AAC_MAIN(1, "AAC Main Profile", true), AAC_LC(
        2,
        "AAC Low Complexity",
        true
    ),
    AAC_SSR(3, "AAC Scalable Sample Rate", false), AAC_LTP(4, "AAC Long Term Prediction", false), AAC_SBR(
        5,
        "AAC SBR",
        true
    ),
    AAC_SCALABLE(6, "Scalable AAC", false), TWIN_VQ(7, "TwinVQ", false), AAC_LD(11, "AAC Low Delay", false), ER_AAC_LC(
        17,
        "Error Resilient AAC Low Complexity",
        true
    ),
    ER_AAC_SSR(18, "Error Resilient AAC SSR", false), ER_AAC_LTP(
        19,
        "Error Resilient AAC Long Term Prediction",
        false
    ),
    ER_AAC_SCALABLE(20, "Error Resilient Scalable AAC", false), ER_TWIN_VQ(
        21,
        "Error Resilient TwinVQ",
        false
    ),
    ER_BSAC(22, "Error Resilient BSAC", false), ER_AAC_LD(23, "Error Resilient AAC Low Delay", false);

    /**
     * Returns a string representation of this profile. The method is
     * identical to `getDescription()`.
     * @return the profile's description
     */
    override fun toString(): String {
        return description
    }

    val isErrorResilientProfile: Boolean
        /**
         * Returns a boolean, indicating if this profile contains error resilient
         * tools. That is, if it's index is higher than 16, since the first error
         * resilient profile is ER_AAC_LC (17).
         * This method is mainly used internally.
         * @return true if the profile uses error resilience
         */
        get() = index > 16

    companion object {
        private val ALL = arrayOf(
            AAC_MAIN,
            AAC_LC,
            AAC_SSR,
            AAC_LTP,
            AAC_SBR,
            AAC_SCALABLE,
            TWIN_VQ,
            null,
            null,
            null,
            AAC_LD,
            null,
            null,
            null,
            null,
            null,
            ER_AAC_LC,
            ER_AAC_SSR,
            ER_AAC_LTP,
            ER_AAC_SCALABLE,
            ER_TWIN_VQ,
            ER_BSAC,
            ER_AAC_LD
        )

        /**
         * Returns a profile instance for the given index. If the index is not
         * between 1 and 23 inclusive, UNKNOWN is returned.
         * @return a profile with the given index
         */
        @JvmStatic
        fun forInt(i: Int): Profile? {
            val p: Profile?
            p =
                if (i <= 0 || i > ALL.size) UNKNOWN else ALL[i - 1]
            return p
        }
    }
}
