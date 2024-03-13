package dev.uninit.mewsic.engine.ext

import dev.uninit.mewsic.engine.FFTPlan
import dev.uninit.mewsic.engine.filter.Filter

fun Filter.response(): Pair<FloatArray, FloatArray> {
    val size = 1024
    val plan = FFTPlan(size)
    val real = FloatArray(size) { 0f }
    real[0] = 1f
    val cplx = FloatArray(size) { 0f }

    process(real)
    reset()

    plan.execute(real, cplx)

    return real to cplx
}
