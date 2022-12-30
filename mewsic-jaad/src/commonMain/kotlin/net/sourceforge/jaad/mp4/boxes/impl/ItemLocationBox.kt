package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The item location box provides a directory of resources in this or other
 * files, by locating their containing file, their offset within that file, and
 * their length. Placing this in binary format enables common handling of this
 * data, even by systems which do not understand the particular metadata system
 * (handler) used. For example, a system might integrate all the externally
 * referenced metadata resources into one file, re-adjusting file offsets and
 * file references accordingly.
 *
 * Items may be stored fragmented into extents, e.g. to enable interleaving. An
 * extent is a contiguous subset of the bytes of the resource; the resource is
 * formed by concatenating the extents. If only one extent is used then either
 * or both of the offset and length may be implied:
 *
 *  * If the offset is not identified (the field has a length of zero), then
 * the beginning of the file (offset 0) is implied.
 *  * If the length is not specified, or specified as zero, then the entire
 * file length is implied. References into the same file as this metadata, or
 * items divided into more than one extent, should have an explicit offset and
 * length, or use a MIME type requiring a different interpretation of the file,
 * to avoid infinite recursion.
 *
 * The size of the item is the sum of the extent lengths.
 *
 * The data-reference index may take the value 0, indicating a reference into
 * the same file as this metadata, or an index into the data-reference table.
 *
 * Some referenced data may itself use offset/length techniques to address
 * resources within it (e.g. an MP4 file might be 'included' in this way).
 * Normally such offsets are relative to the beginning of the containing file.
 * The field 'base offset' provides an additional offset for offset calculations
 * within that contained data. For example, if an MP4 file is included within a
 * file formatted to this specification, then normally data-offsets within that
 * MP4 section are relative to the beginning of file; the base offset adds to
 * those offsets.
 *
 * @author in-somnia
 */
class ItemLocationBox : FullBox("Item Location Box") {
    /**
     * The item ID is an arbitrary integer 'name' for this resource which can be
     * used to refer to it (e.g. in a URL).
     *
     * @return the item ID
     */
    val itemID: IntArray

    /**
     * The data reference index is either zero ('this file') or a 1-based index
     * into the data references in the data information box.
     *
     * @return the data reference index
     */
    var dataReferenceIndex: IntArray
        private set

    /**
     * The base offset provides a base value for offset calculations within the
     * referenced data.
     *
     * @return the base offsets for all items
     */
    var baseOffset: LongArray
        private set

    /**
     * The extent offset provides the absolute offset in bytes from the
     * beginning of the containing file, of this item.
     *
     * @return the offsets for all extents in all items
     */
    var extentOffset: Array<LongArray?>
        private set

    /**
     * The extends length provides the absolute length in bytes of this metadata
     * item. If the value is 0, then length of the item is the length of the
     * entire referenced file.
     *
     * @return the lengths for all extends in all items
     */
    var extentLength: Array<LongArray?>
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)

        /*4 bits offsetSize
		4 bits lengthSize
		4 bits baseOffsetSize
		4 bits reserved
		 */
        val l: Long = `in`.readBytes(2)
        val offsetSize = (l shr 12).toInt() and 0xF
        val lengthSize = (l shr 8).toInt() and 0xF
        val baseOffsetSize = (l shr 4).toInt() and 0xF
        val itemCount = `in`.readBytes(2) as Int
        dataReferenceIndex = IntArray(itemCount)
        baseOffset = LongArray(itemCount)
        extentOffset = arrayOfNulls(itemCount)
        extentLength = arrayOfNulls(itemCount)
        var j: Int
        var extentCount: Int
        for (i in 0 until itemCount) {
            itemID[i] = `in`.readBytes(2) as Int
            dataReferenceIndex[i] = `in`.readBytes(2) as Int
            baseOffset[i] = `in`.readBytes(baseOffsetSize)
            extentCount = `in`.readBytes(2) as Int
            extentOffset[i] = LongArray(extentCount)
            extentLength[i] = LongArray(extentCount)
            j = 0
            while (j < extentCount) {
                extentOffset[i]!![j] = `in`.readBytes(offsetSize)
                extentLength[i]!![j] = `in`.readBytes(lengthSize)
                j++
            }
        }
    }
}
