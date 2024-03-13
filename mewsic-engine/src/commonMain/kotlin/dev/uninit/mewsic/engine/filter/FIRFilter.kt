package dev.uninit.mewsic.engine.filter

class FIRFilter(coefficients: Coefficients) : Filter {
    class Coefficients(val zeros: FloatArray) {
        fun filter() = FIRFilter(this)

        companion object {
            inline operator fun invoke(vararg zeros: Float) = Coefficients(zeros)
        }
    }

    var coefficients = coefficients
        set(value) {
            field = value
            reset()
        }

    private val order = coefficients.zeros.size - 1
    private var xHist = FloatArray(order) { 0f }

    override fun process(frame: FloatArray) {
        for (i in frame.indices) {
            frame[i] = processSample(frame[i])
        }
    }

    override fun processSample(input: Float): Float {
        var output = 0f

        for (i in 1..order) {
            output += coefficients.zeros[i] * xHist[i - 1]
        }

        output = (output + input * coefficients.zeros[0])

        for (i in order - 1 downTo 1) {
            xHist[i] = xHist[i - 1]
        }

        xHist[0] = input

        return output
    }

    override fun reset() {
        xHist.fill(0f)
    }
}
