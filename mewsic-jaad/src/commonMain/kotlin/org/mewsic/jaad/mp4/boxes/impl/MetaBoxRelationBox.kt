package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

/**
 * The metabox relation box indicates a relation between two meta boxes at the
 * same level, i.e., the top level of the file, the Movie Box, or Track Box. The
 * relation between two meta boxes is unspecified if there is no metabox
 * relation box for those meta boxes. Meta boxes are referenced by specifying
 * their handler types.
 *
 * @author in-somnia
 */
class MetaBoxRelationBox : FullBox("Meta Box Relation Box") {
    /**
     * The first meta box to be related.
     */
    var firstMetaboxHandlerType: Long = 0
        private set

    /**
     * The second meta box to be related.
     */
    var secondMetaboxHandlerType: Long = 0
        private set

    /**
     * The metabox relation indicates the relation between the two meta boxes.
     * The following values are defined:
     *
     *  1. The relationship between the boxes is unknown (which is the default
     * when this box is not present)
     *  1. the two boxes are semantically un-related (e.g., one is presentation,
     * the other annotation)
     *  1. the two boxes are semantically related but complementary (e.g., two
     * disjoint sets of meta-data expressed in two different meta-data systems)
     *
     *  1. the two boxes are semantically related but overlap (e.g., two sets of
     * meta-data neither of which is a subset of the other); neither is
     * 'preferred' to the other
     *  1. the two boxes are semantically related but the second is a proper
     * subset or weaker version of the first; the first is preferred
     *  1. the two boxes are semantically related and equivalent (e.g., two
     * essentially identical sets of meta-data expressed in two different
     * meta-data systems)
     *
     */
    var metaboxRelation = 0
        private set

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        firstMetaboxHandlerType = `in`.readBytes(4)
        secondMetaboxHandlerType = `in`.readBytes(4)
        metaboxRelation = `in`.read()
    }
}
