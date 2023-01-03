package net.sourceforge.jaad.mp4.boxes.impl.fd

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox

/**
 * The FD item information box is optional, although it is mandatory for files
 * using FD hint tracks. It provides information on the partitioning of source
 * files and how FD hint tracks are combined into FD sessions. Each partition
 * entry provides details on a particular file partitioning, FEC encoding and
 * associated FEC reservoirs. It is possible to provide multiple entries for one
 * source file (identified by its item ID) if alternative FEC encoding schemes
 * or partitionings are used in the file. All partition entries are implicitly
 * numbered and the first entry has number 1.
 *
 * @author in-somnia
 */
class FDItemInformationBox : FullBox("FD Item Information Box") {
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(2).toInt()
        readChildren(`in`, entryCount) //partition entries
        readChildren(`in`) //FDSessionGroupBox and GroupIDToNameBox
    }
}
