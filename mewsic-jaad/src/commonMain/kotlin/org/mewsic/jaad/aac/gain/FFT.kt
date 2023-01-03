package org.mewsic.jaad.aac.gain

import kotlin.math.log2
import kotlin.math.round

//complex FFT of length 128/16, inplace
internal object FFT {
    private val FFT_TABLE_128 = arrayOf(
        floatArrayOf(1.0f, -0.0f),
        floatArrayOf(0.99879545f, -0.049067676f),
        floatArrayOf(0.9951847f, -0.09801714f),
        floatArrayOf(0.9891765f, -0.14673047f),
        floatArrayOf(0.98078525f, -0.19509032f),
        floatArrayOf(0.97003126f, -0.24298018f),
        floatArrayOf(0.95694035f, -0.29028466f),
        floatArrayOf(0.94154406f, -0.33688986f),
        floatArrayOf(0.9238795f, -0.38268343f),
        floatArrayOf(0.9039893f, -0.42755508f),
        floatArrayOf(0.8819213f, -0.47139674f),
        floatArrayOf(0.8577286f, -0.51410276f),
        floatArrayOf(0.8314696f, -0.55557024f),
        floatArrayOf(0.8032075f, -0.5956993f),
        floatArrayOf(0.77301043f, -0.6343933f),
        floatArrayOf(0.7409511f, -0.671559f),
        floatArrayOf(0.70710677f, -0.70710677f),
        floatArrayOf(0.671559f, -0.7409511f),
        floatArrayOf(0.6343933f, -0.77301043f),
        floatArrayOf(0.5956993f, -0.8032075f),
        floatArrayOf(0.55557024f, -0.8314696f),
        floatArrayOf(0.51410276f, -0.8577286f),
        floatArrayOf(0.47139674f, -0.8819213f),
        floatArrayOf(0.42755508f, -0.9039893f),
        floatArrayOf(0.38268343f, -0.9238795f),
        floatArrayOf(0.33688986f, -0.94154406f),
        floatArrayOf(0.29028466f, -0.95694035f),
        floatArrayOf(0.24298018f, -0.97003126f),
        floatArrayOf(0.19509032f, -0.98078525f),
        floatArrayOf(0.14673047f, -0.9891765f),
        floatArrayOf(0.09801714f, -0.9951847f),
        floatArrayOf(0.049067676f, -0.99879545f),
        floatArrayOf(6.123234E-17f, -1.0f),
        floatArrayOf(-0.049067676f, -0.99879545f),
        floatArrayOf(-0.09801714f, -0.9951847f),
        floatArrayOf(-0.14673047f, -0.9891765f),
        floatArrayOf(-0.19509032f, -0.98078525f),
        floatArrayOf(-0.24298018f, -0.97003126f),
        floatArrayOf(-0.29028466f, -0.95694035f),
        floatArrayOf(-0.33688986f, -0.94154406f),
        floatArrayOf(-0.38268343f, -0.9238795f),
        floatArrayOf(-0.42755508f, -0.9039893f),
        floatArrayOf(-0.47139674f, -0.8819213f),
        floatArrayOf(-0.51410276f, -0.8577286f),
        floatArrayOf(-0.55557024f, -0.8314696f),
        floatArrayOf(-0.5956993f, -0.8032075f),
        floatArrayOf(-0.6343933f, -0.77301043f),
        floatArrayOf(-0.671559f, -0.7409511f),
        floatArrayOf(-0.70710677f, -0.70710677f),
        floatArrayOf(-0.7409511f, -0.671559f),
        floatArrayOf(-0.77301043f, -0.6343933f),
        floatArrayOf(-0.8032075f, -0.5956993f),
        floatArrayOf(-0.8314696f, -0.55557024f),
        floatArrayOf(-0.8577286f, -0.51410276f),
        floatArrayOf(-0.8819213f, -0.47139674f),
        floatArrayOf(-0.9039893f, -0.42755508f),
        floatArrayOf(-0.9238795f, -0.38268343f),
        floatArrayOf(-0.94154406f, -0.33688986f),
        floatArrayOf(-0.95694035f, -0.29028466f),
        floatArrayOf(-0.97003126f, -0.24298018f),
        floatArrayOf(-0.98078525f, -0.19509032f),
        floatArrayOf(-0.9891765f, -0.14673047f),
        floatArrayOf(-0.9951847f, -0.09801714f),
        floatArrayOf(-0.99879545f, -0.049067676f)
    )
    private val FFT_TABLE_16 = arrayOf(
        floatArrayOf(1.0f, -0.0f),
        floatArrayOf(0.9238795f, -0.38268343f),
        floatArrayOf(0.70710677f, -0.70710677f),
        floatArrayOf(0.38268343f, -0.9238795f),
        floatArrayOf(6.123234E-17f, -1.0f),
        floatArrayOf(-0.38268343f, -0.9238795f),
        floatArrayOf(-0.70710677f, -0.70710677f),
        floatArrayOf(-0.9238795f, -0.38268343f)
    )

    fun process(`in`: Array<FloatArray>, n: Int) {
        val ln: Int = round(log2(n.toDouble())).toInt()
        val table = if (n == 128) FFT_TABLE_128 else FFT_TABLE_16

        //bit-reversal
        val rev = Array(n) { FloatArray(2) }
        var i: Int
        var ii = 0
        i = 0
        while (i < n) {
            rev[i][0] = `in`[ii][0]
            rev[i][1] = `in`[ii][1]
            var k = n shr 1
            while (ii >= k && k > 0) {
                ii -= k
                k = k shr 1
            }
            ii += k
            i++
        }
        i = 0
        while (i < n) {
            `in`[i][0] = rev[i][0]
            `in`[i][1] = rev[i][1]
            i++
        }

        //calculation
        var blocks = n / 2
        var size = 2
        var j: Int
        var k: Int
        var l: Int
        var k0: Int
        var k1: Int
        var size2: Int
        val a = FloatArray(2)
        i = 0
        while (i < ln) {
            size2 = size / 2
            k0 = 0
            k1 = size2
            j = 0
            while (j < blocks) {
                l = 0
                k = 0
                while (k < size2) {
                    a[0] = `in`[k1][0] * table[l][0] - `in`[k1][1] * table[l][1]
                    a[1] = `in`[k1][0] * table[l][1] + `in`[k1][1] * table[l][0]
                    `in`[k1][0] = `in`[k0][0] - a[0]
                    `in`[k1][1] = `in`[k0][1] - a[1]
                    `in`[k0][0] += a[0]
                    `in`[k0][1] += a[1]
                    l += blocks
                    k0++
                    k1++
                    ++k
                }
                k0 += size2
                k1 += size2
                ++j
            }
            blocks = blocks / 2
            size = size * 2
            i++
        }
    }
}
