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

abstract class Box(protected val reader: SeekableBinaryReader) {
    protected val nested = mutableMapOf<String, MutableList<Long>>()

    fun scanNested() {
        while (reader.position() < reader.length()) {
            val at = reader.position()
            val size = reader.readUInt().toLong()
            val type = reader.readString(4)
            val list = nested.getOrPut(type, ::mutableListOf)
            list.add(at)
            reader.skip(size - 8)
        }
    }

    fun <T: Box> getAll(type: String, constructor: (SeekableBinaryReader) -> T): List<T> {
        val list = nested[type] ?: return emptyList()
        return list.map { at ->
            reader.seek(at + 8)
            constructor(reader)
        }
    }
}

class Ftyp(reader: SeekableBinaryReader) : Box(reader) {
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

class Moov(reader: SeekableBinaryReader) : Box(reader) {
    val mvhd: Mvhd
    val trak: List<Trak>

    init {
        scanNested()
        mvhd = getAll("mvhd", ::Mvhd).first()
        trak = getAll("trak", ::Trak)
    }
}

class Mvhd(reader: SeekableBinaryReader) : Box(reader) {
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

class Trak(reader: SeekableBinaryReader) : Box(reader) {
    val tkhd: Tkhd
    val mdia: Mdia

    init {
        scanNested()
        tkhd = getAll("tkhd", ::Tkhd).first()
        mdia = getAll("mdia", ::Mdia).first()
    }
}

class Tkhd(reader: SeekableBinaryReader) : Box(reader) {
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

class Mdia(reader: SeekableBinaryReader) : Box(reader) {
    val mdhd: Mdhd
    val hdlr: Hdlr
    val minf: Minf

    init {
        scanNested()
        mdhd = getAll("mdhd", ::Mdhd).first()
        hdlr = getAll("hdlr", ::Hdlr).first()
        minf = getAll("minf") { Minf(it, hdlr) }.first()
    }
}

class Mdhd(reader: SeekableBinaryReader) : Box(reader) {
    // TODO
}

class Hdlr(reader: SeekableBinaryReader) : Box(reader) {
    // TODO
}

class Minf(reader: SeekableBinaryReader, hdlr: Hdlr) : Box(reader) {
    // TODO
}



// To be used for things we don't care about like mvex or iods
object EMPTY : Box(SeekableBinaryReader(ByteArrayInputStream(ByteArray(0))))

fun BinaryReader.readBox(): Box {
    val size = readInt()
    val type = readString(4)

    val data = read(size - 8)
    val reader = SeekableBinaryReader(ByteArrayInputStream(data), false)

    // Top-level boxes only!
    return when (type) {
        "ftyp" -> Ftyp(reader)
        "moov" -> Moov(reader)

        // Not implemented:
        else -> {
            println("Unknown box type: $type")
            EMPTY
        }
    }
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
