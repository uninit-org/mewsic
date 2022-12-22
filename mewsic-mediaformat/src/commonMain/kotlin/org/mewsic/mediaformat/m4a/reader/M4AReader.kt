package org.mewsic.mediaformat.m4a.reader

import org.mewsic.commons.binary.BinaryReader
import org.mewsic.commons.binary.SeekableBinaryReader
import org.mewsic.commons.streams.ByteArrayInputStream
import org.mewsic.commons.streams.EndOfStreamException
import org.mewsic.commons.streams.api.InputStream

class FullHeader(reader: SeekableBinaryReader) {
    val version = reader.readUByte().toInt()
    val flags = reader.readEndian(3).toInt()
}

sealed interface Box

class Ftyp(reader: SeekableBinaryReader) : Box {
    val majorBrand = reader.readString(4)
    val minorVersion = reader.readInt()
    val compatibleBrands: List<String>

    init {
        val compatibleBrands = mutableListOf<String>()
        while (reader.position() < reader.length()) {
            compatibleBrands.add(reader.readString(4))
        }
        this.compatibleBrands = compatibleBrands
    }
}

class Moov(reader: SeekableBinaryReader) : Box {
    val mvhd: Mvhd
    val trak: List<Trak>

    init {
        val nested = reader.readNestedBoxes()
        mvhd = nested.filterIsInstance<Mvhd>().first()
        trak = nested.filterIsInstance<Trak>()
    }
}

class Mvhd(reader: SeekableBinaryReader) : Box {
    val fullHeader = FullHeader(reader)
    val creationTime: Long
    val modificationTime: Long
    val timescale: Int
    val duration: Long
    val rate: Int
    val volume: Int
    val matrix: Array<Int>
    val nextTrackId: Int

    init {
        if (fullHeader.version == 1) {
            creationTime = reader.readLong()
            modificationTime = reader.readLong()
            timescale = reader.readInt()
            duration = reader.readLong()
        } else {
            creationTime = reader.readInt().toLong()
            modificationTime = reader.readInt().toLong()
            timescale = reader.readInt()
            duration = reader.readInt().toLong()
        }
        rate = reader.readInt()
        volume = reader.readShort().toInt()
        reader.skip(10)
        matrix = Array(9) { reader.readInt() }
        reader.skip(24)
        nextTrackId = reader.readInt()
    }

}

class Trak(reader: SeekableBinaryReader) : Box {
    val tkhd: Tkhd
    val mdia: Mdia

    init {
        val nested = reader.readNestedBoxes()
        tkhd = nested.filterIsInstance<Tkhd>().first()
        mdia = nested.filterIsInstance<Mdia>().first()
    }
}

class Tkhd(reader: SeekableBinaryReader) : Box {
    val fullHeader = FullHeader(reader)
    val creationTime: Long
    val modificationTime: Long
    val trackId: Int
    val duration: Long
    val layer: Int
    val alternateGroup: Int
    val volume: Int
    val matrix: Array<Int>
    val width: Int
    val height: Int

    init {
        if (fullHeader.version == 1) {
            creationTime = reader.readLong()
            modificationTime = reader.readLong()
            trackId = reader.readInt()
            reader.skip(4)
            duration = reader.readLong()
        } else {
            creationTime = reader.readInt().toLong()
            modificationTime = reader.readInt().toLong()
            trackId = reader.readInt()
            reader.skip(4)
            duration = reader.readInt().toLong()
        }
        reader.skip(8)
        layer = reader.readShort().toInt()
        alternateGroup = reader.readShort().toInt()
        volume = reader.readShort().toInt()
        reader.skip(2)
        matrix = Array(9) { reader.readInt() }
        val width = reader.readInt()
        val height = reader.readInt()

        this.width = (((width) shr 15) + 1) shr 1
        this.height = (((height) shr 15) + 1) shr 1
    }
}

class Mdia(reader: SeekableBinaryReader) : Box {
    // I just learned hdlr specifies how to parse minf, making it impossible to parse the way we currently do
    // TODO: Rewrite to scan for children first?
//    val mdhd: Mdhd
//    val hdlr: Hdlr
//    val minf: Minf
//
//    init {
//        val nested = reader.readNestedBoxes()
//        mdhd = nested.filterIsInstance<Mdhd>().first()
//        hdlr = nested.filterIsInstance<Hdlr>().first()
//        minf = nested.filterIsInstance<Minf>().first()
//    }
}

// To be used for things we don't care about like mvex or iods
object EMPTY : Box

fun BinaryReader.readBox(): Box {
    val size = readInt()
    val type = readString(4)

    val data = read(size - 8)
    val reader = SeekableBinaryReader(ByteArrayInputStream(data), false)

    return when (type) {
        // Top-level boxes:
        "ftyp" -> Ftyp(reader)
        "moov" -> Moov(reader)

        // Nested boxes:
        "tkhd" -> Tkhd(reader)
        "mdia" -> Mdia(reader)

        // Not implemented:
        else -> {
            println("Unknown box type: $type")
            EMPTY
        }
    }
}

fun SeekableBinaryReader.readNestedBoxes(): List<Box> {
    val boxes = mutableListOf<Box>()
    while (position() < length()) {
        boxes.add(readBox())
    }
    return boxes
}


class M4AReader(stream: InputStream) {
    private val reader = BinaryReader(stream, false)

    fun read() {
        val boxes = mutableListOf<Box>()
        try {
            while (true) {
                val box = reader.readBox()
                boxes.add(box)
            }
        } catch (e: EndOfStreamException) { }
        println(boxes)

        TODO("Implement remainder of read function")
    }
}
