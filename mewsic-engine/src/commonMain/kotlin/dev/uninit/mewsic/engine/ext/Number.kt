package dev.uninit.mewsic.engine.ext

import dev.uninit.mewsic.engine.unit.Angle
import dev.uninit.mewsic.engine.unit.Complex
import dev.uninit.mewsic.engine.unit.Gain

val Number.dB: Gain
    get() = Gain(this.toFloat())

val Number.radians: Angle
    get() = Angle(this.toFloat())

val Number.degrees: Angle
    get() = Angle.fromDegrees(this.toFloat())

val Number.j: Complex
    get() = Complex(0.0f, this.toFloat())

infix fun Number.j(other: Number): Complex {
    return Complex(this.toFloat(), other.toFloat())
}

fun Int.reverseBits(): Int {
    var v = this
    v = (v and 0x55555555 shl 1) or (v ushr 1 and 0x55555555)
    v = (v and 0x33333333 shl 2) or (v ushr 2 and 0x33333333)
    v = (v and 0x0f0f0f0f shl 4) or (v ushr 4 and 0x0f0f0f0f)
    v = (v shl 24) or (v and 0xff00 shl 8) or (v ushr 8 and 0xff00) or (v ushr 24)
    return v
}
