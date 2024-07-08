package dev.uninit.mewsic.app.ext

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

private fun Color.toHSV(): Triple<Float, Float, Float> {
    // H in [0 .. 360]
    // S in [0 .. 1]
    // V in [0 .. 1]
    val argb = toArgb()
    val r = argb shr 16 and 0xFF
    val g = argb shr 8 and 0xFF
    val b = argb and 0xFF
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = (max - min).toFloat()

    var h = when {
        delta == 0f -> 0f
        max == r -> 60f * ((g - b) / delta % 6)
        max == g -> 60f * ((b - r) / delta + 2)
        else -> 60f * ((r - g) / delta + 4)
    }

    if (h < 0) h += 360f

    val s = if (max == 0) 0f else delta / max
    val v = max / 255f

    return Triple(h, s, v)
}

fun Color.withHue(hue: Float): Color {
    val hsv = this.toHSV()
    return Color.hsv(hue, hsv.second, hsv.third)
}

fun Color.withSaturation(saturation: Float): Color {
    val hsv = this.toHSV()
    return Color.hsv(hsv.first, saturation, hsv.third)
}

fun Color.withValue(value: Float): Color {
    val hsv = this.toHSV()
    return Color.hsv(hsv.first, hsv.second, value)
}

fun Color.withHueDelta(hue: Float): Color {
    val hsv = this.toHSV()
    return Color.hsv((hsv.first + hue).coerceIn(0f, 360f), hsv.second, hsv.third)
}

fun Color.withSaturationDelta(saturation: Float): Color {
    val hsv = this.toHSV()
    return Color.hsv(hsv.first, (hsv.second + saturation).coerceIn(0f, 1f), hsv.third)
}

fun Color.withValueDelta(value: Float): Color {
    val hsv = this.toHSV()
    return Color.hsv(hsv.first, hsv.second, (hsv.third + value).coerceIn(0f, 1f))
}

fun Color.withSaturationFactor(factor: Float): Color {
    val hsv = this.toHSV()
    return Color.hsv(hsv.first, (hsv.second * factor).coerceIn(0f, 1f), hsv.third)
}

fun Color.withValueFactor(factor: Float): Color {
    val hsv = this.toHSV()
    return Color.hsv(hsv.first, hsv.second, (hsv.third * factor).coerceIn(0f, 1f))
}

@Deprecated("Use withSaturationDelta and withValueDelta instead", ReplaceWith("this.withSaturationDelta(sat).withValueDelta(value)"), level = DeprecationLevel.ERROR)
fun Color.withSatValue(sat: Float, value: Float): Color {
    val hsv = this.toHSV()
    return Color.hsv(hsv.first, (hsv.second + sat).coerceIn(0f, 1f), (hsv.third + value).coerceIn(0f, 1f))
}
