package net.sourceforge.jaad.mp4.boxes.impl
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream
import net.sourceforge.jaad.mp4.boxes.FullBox

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
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        val entryCount = `in`.readBytes(4).toInt()
        readChildren(`in`, entryCount) //DataEntryUrlBox, DataEntryUrnBox
    }
}
