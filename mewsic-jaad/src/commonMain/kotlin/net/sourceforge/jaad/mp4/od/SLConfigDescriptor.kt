package net.sourceforge.jaad.mp4.od
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.MP4InputStream
import kotlin.math.ceil

//ISO 14496-1 - 10.2.3
//TODO: not working: reads too much! did the specification change?
class SLConfigDescriptor : net.sourceforge.jaad.mp4.od.Descriptor() {
    private var useAccessUnitStart = false
    private var useAccessUnitEnd = false
    private var useRandomAccessPoint = false
    private var usePadding = false
    private var useTimeStamp = false
    private var useWallClockTimeStamp = false
    private var useIdle = false
    private var duration = false
    private var timeStampResolution: Long = 0
    private var ocrResolution: Long = 0
    private var timeStampLength = 0
    private var ocrLength = 0
    private var instantBitrateLength = 0
    private var degradationPriorityLength = 0
    private var seqNumberLength = 0
    private var timeScale: Long = 0
    private var accessUnitDuration = 0
    private var compositionUnitDuration = 0
    private var wallClockTimeStamp: Long = 0
    private var startDecodingTimeStamp: Long = 0
    private var startCompositionTimeStamp: Long = 0
    private var ocrStream = false
    private var ocrES_ID = 0
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        var tmp: Int
        val predefined = `in`.read() == 1
        if (!predefined) {
            //flags
            tmp = `in`.read()
            useAccessUnitStart = tmp shr 7 and 1 == 1
            useAccessUnitEnd = tmp shr 6 and 1 == 1
            useRandomAccessPoint = tmp shr 5 and 1 == 1
            usePadding = tmp shr 4 and 1 == 1
            useTimeStamp = tmp shr 3 and 1 == 1
            useWallClockTimeStamp = tmp shr 2 and 1 == 1
            useIdle = tmp shr 1 and 1 == 1
            duration = tmp and 1 == 1
            timeStampResolution = `in`.readBytes(4)
            ocrResolution = `in`.readBytes(4)
            timeStampLength = `in`.read()
            ocrLength = `in`.read()
            instantBitrateLength = `in`.read()
            tmp = `in`.read()
            degradationPriorityLength = tmp shr 4 and 15
            seqNumberLength = tmp and 15
            if (duration) {
                timeScale = `in`.readBytes(4)
                accessUnitDuration = `in`.readBytes(2).toInt()
                compositionUnitDuration = `in`.readBytes(2).toInt()
            }
            if (!useTimeStamp) {
                if (useWallClockTimeStamp) wallClockTimeStamp = `in`.readBytes(4)
                tmp = ceil((2 * timeStampLength).toDouble() / 8).toInt()
                val tmp2: Long = `in`.readBytes(tmp)
                val mask = ((1 shl timeStampLength) - 1).toLong()
                startDecodingTimeStamp = tmp2 shr timeStampLength and mask
                startCompositionTimeStamp = tmp2 and mask
            }
        }
        tmp = `in`.read()
        ocrStream = tmp shr 7 and 1 == 1
        if (ocrStream) ocrES_ID = `in`.readBytes(2).toInt()
    }
}
