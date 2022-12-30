package net.sourceforge.jaad.mp4.boxes.impl
import net.sourceforge.jaad.mp4.boxes.FullBox
import net.sourceforge.jaad.mp4.boxes.BoxImpl

import net.sourceforge.jaad.mp4.MP4InputStream

/**
 * The IPMP Control Box may contain IPMP descriptors which may be referenced by
 * any stream in the file.
 *
 * The IPMP ToolListDescriptor is defined in ISO/IEC 14496-1, which conveys the
 * list of IPMP tools required to access the media streams in an ISO Base Media
 * File or meta-box, and may include a list of alternate IPMP tools or
 * parametric descriptions of tools required to access the content.
 *
 * The presence of IPMP Descriptor in this IPMPControlBox indicates that media
 * streams within the file or meta-box are protected by the IPMP Tool described
 * in the IPMP Descriptor. More than one IPMP Descriptors can be carried here,
 * if there are more than one IPMP Tools providing the global governance.
 *
 * @author in-somnia
 */
class IPMPControlBox : FullBox("IPMP Control Box") {
    private /*IPMPToolList*/  var toolList: Descriptor? = null
    private /*IPMP*/  var ipmpDescriptors: Array<Descriptor?>
    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        toolList =  /*(IPMPToolListDescriptor)*/Descriptor.createDescriptor(`in`)
        val count: Int = `in`.read()
        ipmpDescriptors = arrayOfNulls<Descriptor>(count)
        for (i in 0 until count) {
            ipmpDescriptors[i] =  /*(IPMPDescriptor)*/Descriptor.createDescriptor(`in`)
        }
    }

    /**
     * The toollist is an IPMP ToolListDescriptor as defined in ISO/IEC 14496-1.
     *
     * @return the toollist
     */
    fun getToolList(): Descriptor? {
        return toolList
    }

    val iPMPDescriptors: Array<Any?>
        /**
         * The list of contained IPMP Descriptors.
         *
         * @return the IPMP descriptors
         */
        get() = ipmpDescriptors
}
