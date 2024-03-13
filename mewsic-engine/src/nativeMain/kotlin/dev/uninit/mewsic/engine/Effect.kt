package dev.uninit.mewsic.engine

interface Effect : Disposable {
    fun setSampleRate(sampleRate: Int)
    fun process(context: EffectContext): Errno
    fun reset()
}
