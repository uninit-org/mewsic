package net.sourceforge.jaad.mp4.boxes.impl.meta

import net.sourceforge.jaad.mp4.MP4InputStream

class RequirementBox : FullBox("Requirement Box") {
    var requirement: String? = null
        private set

    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        requirement = `in`.readString(getLeft(`in`) as Int)
    }
}
