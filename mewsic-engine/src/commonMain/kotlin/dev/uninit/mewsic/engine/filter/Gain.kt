package dev.uninit.mewsic.engine.filter

import dev.uninit.mewsic.engine.unit.Gain

class Gain(initialGain: Gain) : Filter {
    private var amp = initialGain.amplitude
    var gain = initialGain
        set(value) {
            amp = value.amplitude
            field = value
        }

    override fun process(frame: FloatArray) {
        for (i in frame.indices) {
            frame[i] = processSample(frame[i])
        }
    }

    override fun processSample(input: Float): Float {
        return input * amp
    }

    override fun reset() {
        // No-op
    }
}
