package net.sourceforge.jaad.mp4.api.drm
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.mp4.api.Protection
import net.sourceforge.jaad.mp4.boxes.Box
import net.sourceforge.jaad.mp4.boxes.BoxTypes
import net.sourceforge.jaad.mp4.boxes.impl.drm.FairPlayDataBox
import kotlin.text.decodeToString
class ITunesProtection(sinf: Box) : Protection(sinf) {
    val userID: String
    val userName: String
    val userKey: String
    val privateKey: ByteArray
    val initializationVector: ByteArray

    init {
        val schi: Box = sinf.getChild(BoxTypes.SCHEME_INFORMATION_BOX)!!
        userID = (schi.getChild(BoxTypes.FAIRPLAY_USER_ID_BOX) as FairPlayDataBox).data.decodeToString()

        //username box is filled with 0
        val b: ByteArray = (schi.getChild(BoxTypes.FAIRPLAY_USER_NAME_BOX) as FairPlayDataBox).data
        var i = 0
        while (b[i].toInt() != 0) {
            i++
        }
        userName = b.decodeToString(0, i-1)
        userKey = (schi.getChild(BoxTypes.FAIRPLAY_USER_KEY_BOX) as FairPlayDataBox).data.decodeToString()
        privateKey = (schi.getChild(BoxTypes.FAIRPLAY_PRIVATE_KEY_BOX) as FairPlayDataBox).data
        initializationVector = (schi.getChild(BoxTypes.FAIRPLAY_IV_BOX) as FairPlayDataBox).data
    }

    override val scheme: Scheme
        get() = Scheme.ITUNES_FAIR_PLAY
}
