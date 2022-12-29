package net.sourceforge.jaad.mp4.api.drm

import net.sourceforge.jaad.mp4.api.Protection

class ITunesProtection(sinf: Box) : Protection(sinf) {
    val userID: String
    val userName: String
    val userKey: String
    val privateKey: ByteArray
    val initializationVector: ByteArray

    init {
        val schi: Box = sinf.getChild(BoxTypes.SCHEME_INFORMATION_BOX)
        userID = kotlin.String((schi.getChild(BoxTypes.FAIRPLAY_USER_ID_BOX) as FairPlayDataBox).getData())

        //user name box is filled with 0
        val b: ByteArray = (schi.getChild(BoxTypes.FAIRPLAY_USER_NAME_BOX) as FairPlayDataBox).getData()
        var i = 0
        while (b[i].toInt() != 0) {
            i++
        }
        userName = kotlin.String(b, 0, i - 1)
        userKey = kotlin.String((schi.getChild(BoxTypes.FAIRPLAY_USER_KEY_BOX) as FairPlayDataBox).getData())
        privateKey = (schi.getChild(BoxTypes.FAIRPLAY_PRIVATE_KEY_BOX) as FairPlayDataBox).getData()
        initializationVector = (schi.getChild(BoxTypes.FAIRPLAY_IV_BOX) as FairPlayDataBox).getData()
    }

    override val scheme: net.sourceforge.jaad.mp4.api.Protection.Scheme?
        get() = Scheme.ITUNES_FAIR_PLAY
}
