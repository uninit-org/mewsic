package dev.uninit.mewsic.engine

class Engine : Disposable {
    private var enabled by EngineProperty.GlobalEnabled
    private var samplerate by EngineProperty.GlobalSampleRate

    fun process(inLeft: FloatArray, inRight: FloatArray, outLeft: FloatArray, outRight: FloatArray): Errno {
        if (!enabled) {
            return Errno.OK
        }

        inLeft.copyInto(outLeft)
        inRight.copyInto(outRight)

        val ctx = EffectContext(inLeft, inRight, outLeft, outRight)

        // TODO: Effects in series

        return Errno.OK
    }

    fun <T> getProperty(prop: EngineProperty<T>): T {
        val proxy by prop
        return proxy
    }

    fun <T> setProperty(prop: EngineProperty<T>, value: T): Errno {
        var proxy by prop

        when (prop) {
            EngineProperty.GlobalEnabled -> {
                enabled = value as Boolean
            }

            EngineProperty.GlobalSampleRate -> {
                // Propagate to effects
            }

            else -> {
                // Do nothing
            }
        }

        proxy = value

        return Errno.OK
    }

    fun reset() {
        // Propagate to effects
    }

    override fun dispose() {
        // Propagate to effects
    }
}
