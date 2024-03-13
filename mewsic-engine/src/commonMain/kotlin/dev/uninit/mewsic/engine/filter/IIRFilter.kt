package dev.uninit.mewsic.engine.filter

class IIRFilter(coefficients: Coefficients) : Filter {
    class Coefficients(val zeros: FloatArray, val poles: FloatArray) {
        init {
            require(zeros.size == poles.size) { "Zeros and poles must be the same size, got ${zeros.size} zeros and ${poles.size} poles" }
        }

        fun filter() = IIRFilter(this)
    }

    private val order = coefficients.zeros.size - 1
    var coefficients = coefficients
        set(value) {
            require(value.zeros.size == value.poles.size) { "Zeros and poles must be the same size, got ${value.zeros.size} zeros and ${value.poles.size} poles" }
            require(value.zeros.size == order + 1) { "Zeros and poles for a filter of order $order must be of size ${order + 1}, got ${value.zeros.size}" }

            field = value
            reset()
        }
    private var xHist = FloatArray(order) { 0f }
    private var yHist = FloatArray(order) { 0f }

    override fun process(frame: FloatArray) {
        for (i in frame.indices) {
            frame[i] = processSample(frame[i])
        }
    }

    override fun processSample(input: Float): Float {
        var output = 0f

        for (i in 1..order) {
            output += coefficients.zeros[i] * xHist[i - 1] - coefficients.poles[i] * yHist[i - 1]
        }

        output = (output + input * coefficients.zeros[0]) / coefficients.poles[0]

        for (i in order - 1 downTo 1) {
            xHist[i] = xHist[i - 1]
            yHist[i] = yHist[i - 1]
        }

        xHist[0] = input
        yHist[0] = output

        return output
    }

    override fun reset() {
        xHist.fill(0f)
        yHist.fill(0f)
    }

    companion object {
        fun empty(order: Int): IIRFilter {
            val coeffsPoles = FloatArray(order+1) { 0f }
            val coeffsZeros = FloatArray(order+1) { 0f }
            val coeffs = Coefficients(coeffsPoles, coeffsZeros)
            return IIRFilter(coeffs)
        }
    }
}
