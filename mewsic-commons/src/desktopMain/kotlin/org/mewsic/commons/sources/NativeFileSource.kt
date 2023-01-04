package org.mewsic.commons.sources

import org.mewsic.commons.streams.api.SeekableInputStream
import org.mewsic.commons.sources.api.Source
import org.mewsic.commons.streams.SequenceInputStream
import org.mewsic.commons.streams.api.InputStream
import java.io.File
import java.lang.Long.min

typealias Block = ByteArray

actual class NativeFileSource actual constructor(private val path: String) : Source {
    // jvm implementation
    private var _file = File(path)
    // file with getter and setter
    var file: File
        get() {
            return _file
        }
        set(value) {
            _file = File(path)
        }
    inner class InputStreamAccessor : SeekableInputStream {

        private val _in = _file.inputStream()
        private val _cache: MutableList<Byte> = emptyList<Byte>().toMutableList()
        private var _in_position = 0L
        private var _effective_position = 0L
        private var _backing_indice: Long = 0
        private var _indice: Long
            get()  = _backing_indice
            set(value) {
                _effective_position -= (_backing_indice - value) // im like 99% sure this is right, i did my work on paper just to check.
                _backing_indice = min(value, 0.toLong())
            }
        private val _input_accessor: InputStream = SequenceInputStream(sequence {
            while (true) {
                while (_indice < 0) {
                    _indice += 1
                    _effective_position += 1
                    yield(byteArrayOf(_cache[_cache.size + _indice.toInt() - 1])) // we can add here because _indice is a negative index into the cache
                }

                var b = _in.read()
                _in_position += 1
                _effective_position += 1
                _cache.add(b.toByte())
                if (_indice < 0) {
                    _indice -= 1 // assure the negative-indice is not poisoned by the read and subsequent addition to the cache
                }
                yield(byteArrayOf(b.toByte()))
            }
        })
        override fun read(): Byte {
            return _input_accessor.read()
        }

        override fun read(bytes: ByteArray): Int {
            return _input_accessor.read(bytes)
        }

        override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
            return _input_accessor.read(bytes, offset, length)
        }

        override fun readNBytes(n: Int): ByteArray {
            return _input_accessor.readNBytes(n)
        }

        override fun skip(n: Long): Long {
            return _input_accessor.skip(n) // we can assure that in the SequenceInputStream, the skip will be read into the cache which is what we want.
        }

        override fun seek(offset: Long) {
            _indice = offset - _effective_position
        }

        override fun back(offset: Long) {
            _indice = -offset - _effective_position
        }

        override fun position(): Long {
            return _effective_position
        }

        override fun length(): Long {
            return _file.length()
        }
    }
    actual override fun open(): SeekableInputStream {
        return InputStreamAccessor()
    }

    actual override fun reset() {
        _file = File(path)
    }
}
