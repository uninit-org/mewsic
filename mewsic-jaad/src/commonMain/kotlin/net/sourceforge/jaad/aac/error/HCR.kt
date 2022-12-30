package net.sourceforge.jaad.aac.error
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.aac.huffman.HCB
import net.sourceforge.jaad.aac.syntax.BitStream
import net.sourceforge.jaad.aac.syntax.Constants

/**
 * Huffman Codeword Reordering
 * Decodes spectral data for ICStreams if error resilience is used for
 * section data.
 */
//TODO: needs decodeSpectralDataER() in BitStream
object HCR : Constants {
    private const val NUM_CB = 6
    private const val NUM_CB_ER = 22
    private const val MAX_CB = 32
    private const val VCB11_FIRST = 16
    private const val VCB11_LAST = 31
    private val PRE_SORT_CB_STD = intArrayOf(11, 9, 7, 5, 3, 1)
    private val PRE_SORT_CB_ER =
        intArrayOf(11, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 9, 7, 5, 3, 1)
    private val MAX_CW_LEN = intArrayOf(
        0, 11, 9, 20, 16, 13, 11, 14, 12, 17, 14, 49,
        0, 0, 0, 0, 14, 17, 21, 21, 25, 25, 29, 29, 29, 29, 33, 33, 33, 37, 37, 41
    )

    //bit-twiddling helpers
    private val S = intArrayOf(1, 2, 4, 8, 16)
    private val B = intArrayOf(0x55555555, 0x33333333, 0x0F0F0F0F, 0x00FF00FF, 0x0000FFFF)

    //32 bit rewind and reverse
    private fun rewindReverse(v: Int, len: Int): Int {
        var v = v
        v = v shr S[0] and B[0] or (v shl S[0] and B[0].inv())
        v = v shr S[1] and B[1] or (v shl S[1] and B[1].inv())
        v = v shr S[2] and B[2] or (v shl S[2] and B[2].inv())
        v = v shr S[3] and B[3] or (v shl S[3] and B[3].inv())
        v = v shr S[4] and B[4] or (v shl S[4] and B[4].inv())

        //shift off low bits
        v = v shr 32 - len
        return v
    }

    //64 bit rewind and reverse
    fun rewindReverse64(hi: Int, lo: Int, len: Int): IntArray {
        var hi = hi
        var lo = lo
        val i = IntArray(2)
        if (len <= 32) {
            i[0] = 0
            i[1] = rewindReverse(lo, len)
        } else {
            lo = lo shr S[0] and B[0] or (lo shl S[0] and B[0].inv())
            hi = hi shr S[0] and B[0] or (hi shl S[0] and B[0].inv())
            lo = lo shr S[1] and B[1] or (lo shl S[1] and B[1].inv())
            hi = hi shr S[1] and B[1] or (hi shl S[1] and B[1].inv())
            lo = lo shr S[2] and B[2] or (lo shl S[2] and B[2].inv())
            hi = hi shr S[2] and B[2] or (hi shl S[2] and B[2].inv())
            lo = lo shr S[3] and B[3] or (lo shl S[3] and B[3].inv())
            hi = hi shr S[3] and B[3] or (hi shl S[3] and B[3].inv())
            lo = lo shr S[4] and B[4] or (lo shl S[4] and B[4].inv())
            hi = hi shr S[4] and B[4] or (hi shl S[4] and B[4].inv())

            //shift off low bits
            i[1] = hi shr 64 - len or (lo shl len - 32)
            i[1] = lo shr 64 - len
        }
        return i
    }

    private fun isGoodCB(cb: Int, sectCB: Int): Boolean {
        var b = false
        if (sectCB > HCB.ZERO_HCB && sectCB <= HCB.ESCAPE_HCB || sectCB >= VCB11_FIRST && sectCB <= VCB11_LAST) {
            b = if (cb < HCB.ESCAPE_HCB) sectCB == cb || sectCB == cb + 1 else sectCB == cb
        }
        return b
    }

    //sectionDataResilience = hDecoder->aacSectionDataResilienceFlag
    @Throws(AACException::class)
    fun decodeReorderedSpectralData(
        ics: ICStream,
        `in`: BitStream?,
        spectralData: ShortArray,
        sectionDataResilience: Boolean
    ) {
        val info: ICSInfo = ics.getInfo()
        val windowGroupCount: Int = info.windowGroupCount
        val maxSFB: Int = info.maxSFB
        val swbOffsets: IntArray = info.sWBOffsets
        val swbOffsetMax: Int = info.sWBOffsetMax
        //TODO:
        //final SectionData sectData = ics.getSectionData();
        val sectStart: Array<IntArray>? = null //sectData.getSectStart();
        val sectEnd: Array<IntArray>? = null //sectData.getSectEnd();
        val numSec: IntArray? = null //sectData.getNumSec();
        val sectCB: Array<IntArray>? = null //sectData.getSectCB();
        val sectSFBOffsets: Array<IntArray>? = null //info.getSectSFBOffsets();

        //check parameter
        val spDataLen: Int = ics.reorderedSpectralDataLength
        if (spDataLen == 0) return
        val longestLen: Int = ics.longestCodewordLength
        if (longestLen == 0 || longestLen >= spDataLen) throw AACException("length of longest HCR codeword out of range")

        //create spOffsets
        val spOffsets = IntArray(8)
        val shortFrameLen = spectralData.size / 8
        spOffsets[0] = 0
        var g: Int
        g = 1
        while (g < windowGroupCount) {
            spOffsets[g] = spOffsets[g - 1] + shortFrameLen * info.getWindowGroupLength(g - 1)
            g++
        }
        val codeword = arrayOfNulls<Codeword>(512)
        val segment = arrayOfNulls<BitsBuffer>(512)
        val lastCB: Int
        val preSortCB: IntArray
        if (sectionDataResilience) {
            preSortCB = PRE_SORT_CB_ER
            lastCB = NUM_CB_ER
        } else {
            preSortCB = PRE_SORT_CB_STD
            lastCB = NUM_CB
        }
        var PCWs_done = 0
        var segmentsCount = 0
        var numberOfCodewords = 0
        var bitsread = 0
        var sfb: Int
        var w_idx: Int
        var i: Int
        var thisCB: Int
        var thisSectCB: Int
        var cws: Int
        //step 1: decode PCW's (set 0), and stuff data in easier-to-use format
        for (sortloop in 0 until lastCB) {
            //select codebook to process this pass
            thisCB = preSortCB[sortloop]
            sfb = 0
            while (sfb < maxSFB) {
                w_idx = 0
                while (4 * w_idx < java.lang.Math.min(swbOffsets[sfb + 1], swbOffsetMax) - swbOffsets[sfb]) {
                    g = 0
                    while (g < windowGroupCount) {
                        i = 0
                        while (i < numSec!![g]) {
                            if (sectStart!![g][i] <= sfb && sectEnd!![g][i] > sfb) {
                                /* check whether codebook used here is the one we want to process */
                                thisSectCB = sectCB!![g][i]
                                if (isGoodCB(thisCB, thisSectCB)) {
                                    //precalculation
                                    val sect_sfb_size = sectSFBOffsets!![g][sfb + 1] - sectSFBOffsets[g][sfb]
                                    val inc = if (thisSectCB < HCB.FIRST_PAIR_HCB) 4 else 2
                                    val group_cws_count: Int = 4 * info.getWindowGroupLength(g) / inc
                                    val segwidth: Int = java.lang.Math.min(MAX_CW_LEN[thisSectCB], longestLen)

                                    //read codewords until end of sfb or end of window group
                                    cws = 0
                                    while (cws < group_cws_count && cws + w_idx * group_cws_count < sect_sfb_size) {
                                        val sp =
                                            spOffsets[g] + sectSFBOffsets[g][sfb] + inc * (cws + w_idx * group_cws_count)

                                        //read and decode PCW
                                        if (PCWs_done == 0) {
                                            //read in normal segments
                                            if (bitsread + segwidth <= spDataLen) {
                                                segment[segmentsCount]!!.readSegment(segwidth, `in`!!)
                                                bitsread += segwidth

                                                //Huffman.decodeSpectralDataER(segment[segmentsCount], thisSectCB, spectralData, sp);

                                                //keep leftover bits
                                                segment[segmentsCount]!!.rewindReverse()
                                                segmentsCount++
                                            } else {
                                                //remaining after last segment
                                                if (bitsread < spDataLen) {
                                                    val additional_bits = spDataLen - bitsread
                                                    segment[segmentsCount]!!.readSegment(additional_bits, `in`!!)
                                                    segment[segmentsCount].len += segment[segmentsCount - 1].len
                                                    segment[segmentsCount]!!.rewindReverse()
                                                    if (segment[segmentsCount - 1].len > 32) {
                                                        segment[segmentsCount - 1]!!.bufb =
                                                            (segment[segmentsCount]!!.bufb
                                                                    + segment[segmentsCount - 1]!!.showBits(segment[segmentsCount - 1].len - 32))
                                                        segment[segmentsCount - 1]!!.bufa =
                                                            (segment[segmentsCount]!!.bufa
                                                                    + segment[segmentsCount - 1]!!.showBits(32))
                                                    } else {
                                                        segment[segmentsCount - 1]!!.bufa =
                                                            (segment[segmentsCount]!!.bufa
                                                                    + segment[segmentsCount - 1]!!.showBits(segment[segmentsCount - 1].len))
                                                        segment[segmentsCount - 1]!!.bufb =
                                                            segment[segmentsCount]!!.bufb
                                                    }
                                                    segment[segmentsCount - 1].len += additional_bits
                                                }
                                                bitsread = spDataLen
                                                PCWs_done = 1
                                                codeword[0]!!.fill(sp, thisSectCB)
                                            }
                                        } else {
                                            codeword[numberOfCodewords - segmentsCount]!!.fill(sp, thisSectCB)
                                        }
                                        numberOfCodewords++
                                        cws++
                                    }
                                }
                            }
                            i++
                        }
                        g++
                    }
                    w_idx++
                }
                sfb++
            }
        }
        if (segmentsCount == 0) throw AACException("no segments in HCR")
        val numberOfSets = numberOfCodewords / segmentsCount

        //step 2: decode nonPCWs
        var trial: Int
        var codewordBase: Int
        var segmentID: Int
        var codewordID: Int
        for (set in 1..numberOfSets) {
            trial = 0
            while (trial < segmentsCount) {
                codewordBase = 0
                while (codewordBase < segmentsCount) {
                    segmentID = (trial + codewordBase) % segmentsCount
                    codewordID = codewordBase + set * segmentsCount - segmentsCount

                    //data up
                    if (codewordID >= numberOfCodewords - segmentsCount) break
                    if (codeword[codewordID]!!.decoded == 0 && segment[segmentID].len > 0) {
                        if (codeword[codewordID]!!.bits.len !== 0) segment[segmentID]!!
                            .concatBits(codeword[codewordID]!!.bits!!)
                        val tmplen: Int = segment[segmentID].len
                        /*int ret = Huffman.decodeSpectralDataER(segment[segmentID], codeword[codewordID].cb,
								spectralData, codeword[codewordID].sp_offset);

						if(ret>=0) codeword[codewordID].decoded = 1;
						else {
							codeword[codewordID].bits = segment[segmentID];
							codeword[codewordID].bits.len = tmplen;
						}*/
                    }
                    codewordBase++
                }
                trial++
            }
            i = 0
            while (i < segmentsCount) {
                segment[i]!!.rewindReverse()
                i++
            }
        }
    }

    private class Codeword {
        var cb = 0
        var decoded = 0
        var sp_offset = 0
        var bits: BitsBuffer? = null
        fun fill(sp: Int, cb: Int) {
            sp_offset = sp
            this.cb = cb
            decoded = 0
            bits = BitsBuffer()
        }
    }
}
