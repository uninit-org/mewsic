package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox
import org.mewsic.jaad.mp4.od.Descriptor
import org.mewsic.jaad.mp4.od.ObjectDescriptor

/**
 * The IPMPInfoBox contains IPMP Descriptors which document the protection
 * applied to the stream.
 *
 * The IPMP Descriptor is defined in ISO/IEC 14496-1. This is a part of the
 * MPEG-4 object descriptors (OD) that describe how an object can be accessed
 * and decoded. In the ISO Base Media File Format, IPMP Descriptor can be
 * carried directly in IPMPInfoBox without the need for OD stream.
 *
 * The presence of IPMP Descriptor in this IPMPInfoBox indicates the associated
 * media stream is protected by the IPMP Tool described in the IPMP Descriptor.
 *
 * Each IPMP Descriptor has an IPMP-toolID, which identifies the required IPMP
 * tool for protection. An independent registration authority (RA) is used so
 * any party can register its own IPMP Tool and identify this without
 * collisions.
 *
 * The IPMP Descriptor carries IPMP information for one or more IPMP Tool
 * instances, it includes but not limited to IPMP Rights Data, IPMP Key Data,
 * Tool Configuration Data, etc.
 *
 * More than one IPMP Descriptors can be carried in this IPMPInfoBox if this
 * media stream is protected by more than one IPMP Tools.
 *
 * @author in-somnia
 */
class IPMPInfoBox : FullBox("IPMP Info Box") {
    private var ipmpDescriptors: MutableList<Descriptor?>? = null

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        ipmpDescriptors = mutableListOf()
        /*IPMP*/
        var desc: Descriptor
        while (getLeft(`in`) > 0) {
            desc = ObjectDescriptor.createDescriptor(`in`)
            ipmpDescriptors!!.add(desc)
        }
    }

    val iPMPDescriptors: List<Descriptor?>
        /**
         * The contained list of IPMP descriptors.
         *
         * @return the IPMP descriptors
         */
        get() = ipmpDescriptors!!.toList()
}
