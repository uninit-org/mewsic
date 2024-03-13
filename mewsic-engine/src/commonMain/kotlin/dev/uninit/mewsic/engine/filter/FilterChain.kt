package dev.uninit.mewsic.engine.filter

class FilterChain(private val filters: List<Filter>) : Filter {
    override fun process(frame: FloatArray) {
        for (filter in filters) {
            filter.process(frame)
        }
    }

    override fun processSample(input: Float): Float {
        var output = input
        for (filter in filters) {
            output = filter.processSample(output)
        }
        return output
    }

    override fun reset() {
        for (filter in filters) {
            filter.reset()
        }
    }

    override fun plus(other: Filter): Filter {
        return FilterChain(filters + other)
    }
}
