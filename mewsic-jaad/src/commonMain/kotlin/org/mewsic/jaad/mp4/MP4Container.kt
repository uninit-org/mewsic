package org.mewsic.jaad.mp4

import org.mewsic.jaad.mp4.api.Brand
import org.mewsic.jaad.mp4.api.Movie
import org.mewsic.jaad.mp4.boxes.Box
import org.mewsic.jaad.mp4.boxes.BoxFactory
import org.mewsic.jaad.mp4.boxes.BoxTypes
import org.mewsic.jaad.mp4.boxes.impl.FileTypeBox
import org.mewsic.jaad.mp4.boxes.impl.ProgressiveDownloadInformationBox
import org.mewsic.commons.streams.FileInputStream

/**
 * The MP4Container is the central class for the MP4 demultiplexer. It reads the
 * container and gives access to the containing data.
 *
 * The data source can be either an `InputStream` or a
 * `RandomAccessFile`. Since the specification does not decree a
 * specific order of the content, the data needed for parsing (the sample
 * tables) may be at the end of the stream. In this case, random access is
 * needed and reading from an `InputSteam` will cause an exception.
 * Thus, whenever possible, a `RandomAccessFile` should be used for
 * local files. Parsing from an `InputStream` is useful when reading
 * from a network stream.
 *
 * Each `MP4Container` can return the used file brand (file format
 * version). Optionally, the following data may be present:
 *
 *  * progressive download informations: pairs of download rate and playback
 * delay, see [getDownloadInformationPairs()][.getDownloadInformationPairs]
 *  * a `Movie`
 *
 *
 * Additionally it gives access to the underlying MP4 boxes, that can be
 * retrieved by `getBoxes()`. However, it is not recommended to
 * access the boxes directly.
 *
 * @author in-somnia
 */
class MP4Container {
    private val `in`: MP4InputStream
    private val boxes: MutableList<Box?>
    private var major: Brand? = null
    private var minor: Brand? = null
    private var compatible: Array<Brand?>? = null
    private var ftyp: FileTypeBox? = null
    private var pdin: ProgressiveDownloadInformationBox? = null
    private var moov: Box? = null
    private var movie: Movie? = null
    var isContentRead: Boolean = false

    constructor(`in`: org.mewsic.commons.streams.api.InputStream) {
        this.`in` = MP4InputStream(`in`)
        boxes = mutableListOf()

    }

    constructor(`in`: FileInputStream?) {
        this.`in` = MP4InputStream(`in`)
        boxes = mutableListOf()
        readContent()
    }

    @Throws(Exception::class)
    fun readContent() {
        //read all boxes
        var box: Box? = null
        var type: Long
        var moovFound = false
        while (`in`.hasLeft()) {
            box = BoxFactory.parseBox(null, `in`)
            if (boxes.isEmpty() && box.type != BoxTypes.FILE_TYPE_BOX) throw MP4Exception(
                "no MP4 signature found"
            )

            boxes.add(box)
            type = box.type
            if (type == BoxTypes.FILE_TYPE_BOX) {
                if (ftyp == null) ftyp = box as FileTypeBox?
            } else if (type == BoxTypes.MOVIE_BOX) {
                if (movie == null) moov = box
                moovFound = true
            } else if (type == BoxTypes.PROGRESSIVE_DOWNLOAD_INFORMATION_BOX) {
                if (pdin == null) pdin = box as ProgressiveDownloadInformationBox?
            } else if (type == BoxTypes.MEDIA_DATA_BOX) {
                if (moovFound) break else if (!`in`.hasRandomAccess()) throw MP4Exception("movie box at end of file, need random access")
            }
        }
        isContentRead = true

    }
    fun readContent(t: (eventName: String, args: Map<String, Any>) -> Unit) {
        t("READ_CONTENT", mapOf("message" to "Reading content"))
        var box: Box? = null
        var type: Long
        var moovFound = false
        while (`in`.hasLeft()) {
            box = BoxFactory.parseBox(null, `in`)
            if (boxes.isEmpty() && box.type != BoxTypes.FILE_TYPE_BOX) throw MP4Exception(
                "no MP4 signature found"
            )
            t("BOX", mapOf("box" to box, "type" to box.type))
            boxes.add(box)
            type = box.type
            if (type == BoxTypes.FILE_TYPE_BOX) {
                if (ftyp == null) ftyp = box as FileTypeBox?
            } else if (type == BoxTypes.MOVIE_BOX) {
                if (movie == null) moov = box
                moovFound = true
            } else if (type == BoxTypes.PROGRESSIVE_DOWNLOAD_INFORMATION_BOX) {
                if (pdin == null) pdin = box as ProgressiveDownloadInformationBox?
            } else if (type == BoxTypes.MEDIA_DATA_BOX) {
                if (moovFound) break else if (!`in`.hasRandomAccess()) throw MP4Exception("movie box at end of file, need random access")
            }
        }
        isContentRead = true
        t("OPEN", mapOf("SUCCESS" to isContentRead))
    }

    val majorBrand: Brand?
        get() {
            if (major == null) major = Brand.forID(ftyp!!.majorBrand!!)
            return major
        }
    val minorBrand: Brand?
        get() {
            if (minor == null) minor = Brand.forID(ftyp!!.majorBrand!!)
            return minor
        }
    val compatibleBrands: Array<Brand?>?
        get() {
            if (compatible == null) {
                val s: Array<String> = ftyp!!.compatibleBrands.map { it!! }.toTypedArray()
                compatible = arrayOfNulls<Brand>(s.size)
                for (i in s.indices) {
                    compatible!![i] = Brand.forID(s[i])
                }
            }
            return compatible
        }

    //TODO: pdin, movie fragments??
    fun getMovie(): Movie? {
        if (moov == null) return null else if (movie == null) movie = Movie(moov!!, `in`)
        return movie
    }

    fun getBoxes(): List<Box> {
        return boxes.map { it!! }
    }

    companion object {

    }
}
