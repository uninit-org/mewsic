package dev.uninit.mewsic.engine

class EffectContext(
    val inLeft: FloatArray,
    val inRight: FloatArray,
    val outLeft: FloatArray,
    val outRight: FloatArray,
) {
    constructor(size: Int) : this(
        FloatArray(size),
        FloatArray(size),
        FloatArray(size),
        FloatArray(size),
    )
}
