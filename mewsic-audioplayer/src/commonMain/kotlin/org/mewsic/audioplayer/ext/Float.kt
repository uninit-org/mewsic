package org.mewsic.audioplayer.ext

import kotlin.math.log10
import kotlin.math.pow

// db -> amp
fun Float.toAmplitude(): Float {
    return 10f.pow(this / 20f)
}

// amp -> db
fun Float.toDecibels(): Float {
    return 20f * log10(this)
}
