package org.mewsic.jaad.mp4.boxes.impl.meta

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

class RequirementBox : FullBox("Requirement Box") {
    var requirement: String? = null
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        requirement = `in`.readString(getLeft(`in`).toInt())
    }
}
