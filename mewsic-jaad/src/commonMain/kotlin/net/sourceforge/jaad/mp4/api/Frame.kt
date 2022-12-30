package net.sourceforge.jaad.mp4.api

class Frame internal constructor(type: net.sourceforge.jaad.mp4.api.Type, offset: Long, size: Long, time: Double) :
    Comparable<Frame> {
    private val type: net.sourceforge.jaad.mp4.api.Type
    val offset: Long
    val size: Long
    val time: Double
    lateinit var data: ByteArray

    init {
        this.type = type
        this.offset = offset
        this.size = size
        this.time = time
    }

    fun getType(): net.sourceforge.jaad.mp4.api.Type {
        return type
    }

    override fun compareTo(f: Frame): Int {
        val d = time - f.time
        //0 should not happen, since frames don't have the same timestamps
        return if (d < 0) -1 else if (d > 0) 1 else 0
    }
}
