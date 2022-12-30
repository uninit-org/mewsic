package net.sourceforge.jaad.mp4.boxes.impl.fd
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The FEC reservoir box associates the source file identified in the file
 * partition box with FEC reservoirs stored as additional items. It contains a
 * list that starts with the first FEC reservoir associated with the first
 * source block of the source file and continues sequentially through the source
 * blocks of the source file.
 *
 * @author in-somnia
 */
class FECReservoirBox : FullBox("FEC Reservoir Box") {
    /**
     * The item ID indicates the location of the FEC reservoir associated with a
     * source block.
     *
     * @return all item IDs
     */
    lateinit var itemIDs: IntArray
        private set

    /**
     * The symbol count indicates the number of repair symbols contained in the
     * FEC reservoir.
     *
     * @return all symbol counts
     */
    lateinit var symbolCounts: LongArray
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(2).toInt()
        itemIDs = IntArray(entryCount)
        symbolCounts = LongArray(entryCount)
        for (i in 0 until entryCount) {
            itemIDs[i] = `in`.readBytes(2).toInt()
            symbolCounts[i] = `in`.readBytes(4)
        }
    }
}
