package org.mewsic.commons.lang

object Arrays {
    fun copyOf(original: BooleanArray, newLength: Int): BooleanArray {
        return BooleanArray(newLength) {
            return@BooleanArray original.getOrNull(it) ?: false
        }
    }
    fun copyOf(original: ByteArray, newLength: Int): ByteArray {
        return ByteArray(newLength) {
            return@ByteArray original.getOrNull(it) ?: 0
        }
    }
    fun copyOf(original: ShortArray, newLength: Int): ShortArray {
        return ShortArray(newLength) {
            return@ShortArray original.getOrNull(it) ?: 0
        }
    }
    fun copyOf(original: IntArray, newLength: Int): IntArray {
        return IntArray(newLength) {
            return@IntArray original.getOrNull(it) ?: 0
        }
    }
    fun copyOf(original: LongArray, newLength: Int): LongArray {
        return LongArray(newLength) {
            return@LongArray original.getOrNull(it) ?: 0
        }
    }
    fun copyOf(original: FloatArray, newLength: Int): FloatArray {
        return FloatArray(newLength) {
            return@FloatArray original.getOrNull(it) ?: 0f
        }
    }
    fun copyOf(original: DoubleArray, newLength: Int): DoubleArray {
        return DoubleArray(newLength) {
            return@DoubleArray original.getOrNull(it) ?: 0.0
        }
    }

    fun <T> arraycopy(src: Array<T>, srcPos: Int, dest: Array<T>, destPos: Int, length: Int) {
        for (i in 0 until length) {
            dest[destPos + i] = src[srcPos + i]
        }
    }
    fun arraycopy(src: BooleanArray, srcPos: Int, dest: BooleanArray, destPos: Int, length: Int) {
        for (i in 0 until length) {
            dest[destPos + i] = src[srcPos + i]
        }
    }
    fun arraycopy(src: ByteArray, srcPos: Int, dest: ByteArray, destPos: Int, length: Int) {
        for (i in 0 until length) {
            dest[destPos + i] = src[srcPos + i]
        }
    }
    fun arraycopy(src: ShortArray, srcPos: Int, dest: ShortArray, destPos: Int, length: Int) {
        for (i in 0 until length) {
            dest[destPos + i] = src[srcPos + i]
        }
    }
    fun arraycopy(src: IntArray, srcPos: Int, dest: IntArray, destPos: Int, length: Int) {
        for (i in 0 until length) {
            dest[destPos + i] = src[srcPos + i]
        }
    }
    fun arraycopy(src: LongArray, srcPos: Int, dest: LongArray, destPos: Int, length: Int) {
        for (i in 0 until length) {
            dest[destPos + i] = src[srcPos + i]
        }
    }
    fun arraycopy(src: FloatArray, srcPos: Int, dest: FloatArray, destPos: Int, length: Int) {
        for (i in 0 until length) {
            dest[destPos + i] = src[srcPos + i]
        }
    }
    fun arraycopy(src: DoubleArray, srcPos: Int, dest: DoubleArray, destPos: Int, length: Int) {
        for (i in 0 until length) {
            dest[destPos + i] = src[srcPos + i]
        }
    }

    fun <T> fill(arr: Array<T>, valu: T) {
        for (i in arr.indices) {
            arr[i] = valu
        }
    }
    fun fill(arr: BooleanArray, valu: Boolean) {
        for (i in arr.indices) {
            arr[i] = valu
        }
    }
    fun fill(arr: ByteArray, valu: Byte) {
        for (i in arr.indices) {
            arr[i] = valu
        }
    }
    fun fill(arr: ShortArray, valu: Short) {
        for (i in arr.indices) {
            arr[i] = valu
        }
    }
    fun fill(arr: IntArray, valu: Int) {
        for (i in arr.indices) {
            arr[i] = valu
        }
    }
    fun fill(arr: LongArray, valu: Long) {
        for (i in arr.indices) {
            arr[i] = valu
        }
    }
    fun fill(arr: FloatArray, valu: Float) {
        for (i in arr.indices) {
            arr[i] = valu
        }
    }
    fun fill(arr: DoubleArray, valu: Double) {
        for (i in arr.indices) {
            arr[i] = valu
        }
    }


}
