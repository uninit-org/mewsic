package dev.uninit.mewsic.engine.filter

interface Filter {
    fun process(frame: FloatArray)
    fun processSample(input: Float): Float
    fun reset()

    operator fun plus(other: Filter): Filter = FilterChain(listOf(this, other))
}
