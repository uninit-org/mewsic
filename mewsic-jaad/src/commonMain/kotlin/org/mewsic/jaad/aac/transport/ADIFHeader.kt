package net.sourceforge.jaad.aac.transport

import net.sourceforge.jaad.aac.AACException
import net.sourceforge.jaad.aac.syntax.*

class ADIFHeader private constructor() {
    private var id: Long = 0
    private var copyrightIDPresent = false
    private val copyrightID: ByteArray
    private var originalCopy = false
    private var home = false
    private var bitstreamType = false
    private var bitrate = 0
    private var pceCount = 0
    private var adifBufferFullness: IntArray
    private var pces: Array<PCE?>

    init {
        copyrightID = ByteArray(9)
    }

    @Throws(AACException::class)
    private override fun decode(`in`: BitStream) {
        var i: Int
        id = `in`.readBits(32).toLong() //'ADIF'
        copyrightIDPresent = `in`.readBool()
        if (copyrightIDPresent) {
            i = 0
            while (i < 9) {
                copyrightID[i] = `in`.readBits(8).toByte()
                i++
            }
        }
        originalCopy = `in`.readBool()
        home = `in`.readBool()
        bitstreamType = `in`.readBool()
        bitrate = `in`.readBits(23)
        pceCount = `in`.readBits(4) + 1
        pces = arrayOfNulls(pceCount)
        adifBufferFullness = IntArray(pceCount)
        i = 0
        while (i < pceCount) {
            if (bitstreamType) adifBufferFullness[i] = -1 else adifBufferFullness[i] = `in`.readBits(20)
            pces[i] = PCE()
            pces[i]!!.decode(`in`)
            i++
        }
    }

    val firstPCE: PCE?
        get() = pces[0]

    companion object {
        private const val ADIF_ID: Long = 0x41444946 //'ADIF'

        @Throws(AACException::class)
        fun isPresent(`in`: BitStream): Boolean {
            return `in`.peekBits(32).toLong() == ADIF_ID
        }

        @Throws(AACException::class)
        fun readHeader(`in`: BitStream): ADIFHeader {
            val h = ADIFHeader()
            h.decode(`in`)
            return h
        }
    }
}
