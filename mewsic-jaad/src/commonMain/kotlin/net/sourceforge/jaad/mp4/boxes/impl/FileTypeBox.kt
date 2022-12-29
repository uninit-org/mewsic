package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.boxes.BoxImpl

//TODO: 3gpp brands
class FileTypeBox : BoxImpl("File Type Box") {
    var majorBrand: String? = null
        protected set
    var minorVersion: String? = null
        protected set
    var compatibleBrands: Array<String?>
        protected set

    @Throws(java.io.IOException::class)
    override fun decode(`in`: MP4InputStream) {
        majorBrand = `in`.readString(4)
        minorVersion = `in`.readString(4)
        compatibleBrands = arrayOfNulls(getLeft(`in`) as Int / 4)
        for (i in compatibleBrands.indices) {
            compatibleBrands[i] = `in`.readString(4)
        }
    }

    companion object {
        const val BRAND_ISO_BASE_MEDIA = "isom"
        const val BRAND_ISO_BASE_MEDIA_2 = "iso2"
        const val BRAND_ISO_BASE_MEDIA_3 = "iso3"
        const val BRAND_MP4_1 = "mp41"
        const val BRAND_MP4_2 = "mp42"
        const val BRAND_MOBILE_MP4 = "mmp4"
        const val BRAND_QUICKTIME = "qm  "
        const val BRAND_AVC = "avc1"
        const val BRAND_AUDIO = "M4A "
        const val BRAND_AUDIO_2 = "M4B "
        const val BRAND_AUDIO_ENCRYPTED = "M4P "
        const val BRAND_MP7 = "mp71"
    }
}
