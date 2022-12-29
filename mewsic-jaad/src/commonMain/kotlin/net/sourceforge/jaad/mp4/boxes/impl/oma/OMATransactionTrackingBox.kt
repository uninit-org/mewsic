package net.sourceforge.jaad.mp4.boxes.impl.oma

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The OMA DRM Transaction Tracking Box enables transaction tracking as defined
 * in 'OMA DRM v2.1' section 15.3. The box includes a single transaction-ID and
 * may appear in both DCF and PDCF.
 *
 * @author in-somnia
 */
class OMATransactionTrackingBox : FullBox("OMA DRM Transaction Tracking Box") {
    /**
     * Returns the transaction-ID of the DCF or PDCF respectively.
     *
     * @return the transaction-ID
     */
    var transactionID: String? = null
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        transactionID = `in`.readString(16)
    }
}
