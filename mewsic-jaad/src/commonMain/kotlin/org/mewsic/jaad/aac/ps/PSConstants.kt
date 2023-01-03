package org.mewsic.jaad.aac.ps

internal interface PSConstants {
    companion object {
        const val MAX_PS_ENVELOPES = 5
        const val NO_ALLPASS_LINKS = 3
        const val NEGATE_IPD_MASK = 0x1000
        const val DECAY_SLOPE = 0.05f
        const val COEF_SQRT2 = 1.4142135623731f
    }
}
