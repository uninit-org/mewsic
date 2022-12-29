package org.mewsic.commons.streams

import org.mewsic.commons.sources.NativeFileSource
import org.mewsic.commons.streams.api.SeekableInputStream

class FileInputStream(val path: String) : SeekableInputStream{
    val source: NativeFileSource = NativeFileSource(path)
    val stream: SeekableInputStream = source.open()
    init {

    }

    override fun read(): Byte {
        return stream.read()
    }

    override fun read(bytes: ByteArray): Int {
        TODO("Not yet implemented")
    }

    override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
        TODO("Not yet implemented")
    }

    override fun readNBytes(n: Int): ByteArray {
        TODO("Not yet implemented")
    }

    override fun skip(n: Long): Long {
        TODO("Not yet implemented")
    }

    override fun seek(offset: Long) {
        TODO("Not yet implemented")
    }

    override fun back(offset: Long) {
        TODO("Not yet implemented")
    }

    override fun position(): Long {
        TODO("Not yet implemented")
    }

    override fun length(): Long {
        TODO("Not yet implemented")
    }

}
