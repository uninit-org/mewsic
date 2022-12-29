package net.sourceforge.jaad.mp4

import net.sourceforge.jaad.mp4.api.Brand

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
    private val `in`: net.sourceforge.jaad.mp4.MP4InputStream
    private val boxes: MutableList<Box?>
    private var major: Brand? = null
    private var minor: Brand? = null
    private var compatible: Array<Brand?>?
    private var ftyp: FileTypeBox? = null
    private var pdin: ProgressiveDownloadInformationBox? = null
    private var moov: Box? = null
    private var movie: Movie? = null

    constructor(`in`: java.io.InputStream?) {
        this.`in` = net.sourceforge.jaad.mp4.MP4InputStream(`in`)
        boxes = java.util.ArrayList<Box>()
        readContent()
    }

    constructor(`in`: java.io.RandomAccessFile?) {
        this.`in` = net.sourceforge.jaad.mp4.MP4InputStream(`in`)
        boxes = java.util.ArrayList<Box>()
        readContent()
    }

    @Throws(java.io.IOException::class)
    private fun readContent() {
        //read all boxes
        var box: Box? = null
        var type: Long
        var moovFound = false
        while (`in`.hasLeft()) {
            box = BoxFactory.parseBox(null, `in`)
            if (boxes.isEmpty() && box.getType() !== BoxTypes.FILE_TYPE_BOX) throw net.sourceforge.jaad.mp4.MP4Exception(
                "no MP4 signature found"
            )
            boxes.add(box)
            type = box.getType()
            if (type == BoxTypes.FILE_TYPE_BOX) {
                if (ftyp == null) ftyp = box as FileTypeBox?
            } else if (type == BoxTypes.MOVIE_BOX) {
                if (movie == null) moov = box
                moovFound = true
            } else if (type == BoxTypes.PROGRESSIVE_DOWNLOAD_INFORMATION_BOX) {
                if (pdin == null) pdin = box as ProgressiveDownloadInformationBox?
            } else if (type == BoxTypes.MEDIA_DATA_BOX) {
                if (moovFound) break else if (!`in`.hasRandomAccess()) throw net.sourceforge.jaad.mp4.MP4Exception("movie box at end of file, need random access")
            }
        }
    }

    val majorBrand: net.sourceforge.jaad.mp4.api.Brand?
        get() {
            if (major == null) major = Brand.forID(ftyp.getMajorBrand())
            return major
        }
    val minorBrand: net.sourceforge.jaad.mp4.api.Brand?
        get() {
            if (minor == null) minor = Brand.forID(ftyp.getMajorBrand())
            return minor
        }
    val compatibleBrands: Array<net.sourceforge.jaad.mp4.api.Brand?>?
        get() {
            if (compatible == null) {
                val s: Array<String> = ftyp.getCompatibleBrands()
                compatible = arrayOfNulls<Brand>(s.size)
                for (i in s.indices) {
                    compatible!![i] = Brand.forID(s[i])
                }
            }
            return compatible
        }

    //TODO: pdin, movie fragments??
    fun getMovie(): Movie? {
        if (moov == null) return null else if (movie == null) movie = Movie(moov, `in`)
        return movie
    }

    fun getBoxes(): List<Box> {
        return java.util.Collections.unmodifiableList(boxes)
    }

    companion object {
        init {
            val log: java.util.logging.Logger = java.util.logging.Logger.getLogger("MP4 API")
            for (h in log.getHandlers()) {
                log.removeHandler(h)
            }
            log.setLevel(java.util.logging.Level.WARNING)
            val h: java.util.logging.ConsoleHandler = java.util.logging.ConsoleHandler()
            h.setLevel(java.util.logging.Level.ALL)
            log.addHandler(h)
        }
    }
}
