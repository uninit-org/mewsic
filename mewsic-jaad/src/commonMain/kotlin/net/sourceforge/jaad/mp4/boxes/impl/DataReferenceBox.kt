package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The data reference object contains a table of data references (normally URLs)
 * that declare the location(s) of the media data used within the presentation.
 * The data reference index in the sample description ties entries in this table
 * to the samples in the track. A track may be split over several sources in
 * this way.
 * The data entry is either a DataEntryUrnBox or a DataEntryUrlBox.
 *
 * @author in-somnia
 */
class DataReferenceBox : FullBox("Data Reference Box") {
    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(4) as Int
        readChildren(`in`, entryCount) //DataEntryUrlBox, DataEntryUrnBox
    }
}
