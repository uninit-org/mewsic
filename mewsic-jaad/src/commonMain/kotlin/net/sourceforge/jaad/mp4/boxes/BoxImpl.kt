package net.sourceforge.jaad.mp4.boxes

import net.sourceforge.jaad.mp4.MP4InputStream

open class BoxImpl(override val name: String) : net.sourceforge.jaad.mp4.boxes.Box {
    override var size: Long = 0
        protected set
    override var type: Long = 0
        protected set
    override var offset: Long = 0
        protected set
    override var parent: net.sourceforge.jaad.mp4.boxes.Box? = null
    override val children: MutableList<net.sourceforge.jaad.mp4.boxes.Box>

    init {
        children = ArrayList<net.sourceforge.jaad.mp4.boxes.Box>(4)
    }

    fun setParams(parent: net.sourceforge.jaad.mp4.boxes.Box?, size: Long, type: Long, offset: Long) {
        this.size = size
        this.type = type
        this.parent = parent
        this.offset = offset
    }

    @Throws(Exception::class)
    protected fun getLeft(`in`: MP4InputStream): Long {
        return offset + size - `in`.getOffset()
    }

    /**
     * Decodes the given input stream by reading this box and all of its
     * children (if any).
     *
     * @param in an input stream
     * @throws IOException if an error occurs while reading
     */
    @Throws(Exception::class)
    fun decode(`in`: MP4InputStream?) {
    }

    fun getParent(): net.sourceforge.jaad.mp4.boxes.Box? {
        return parent
    }

    override fun toString(): String {
        return name + " [" + net.sourceforge.jaad.mp4.boxes.BoxFactory.typeToString(type) + "]"
    }

    //container methods
    override fun hasChildren(): Boolean {
        return children.size > 0
    }

    override fun hasChild(type: Long): Boolean {
        var b = false
        for (box in children) {
            if (box.type == type) {
                b = true
                break
            }
        }
        return b
    }

    override fun getChild(type: Long): net.sourceforge.jaad.mp4.boxes.Box? {
        var box: net.sourceforge.jaad.mp4.boxes.Box? = null
        var b: net.sourceforge.jaad.mp4.boxes.Box? = null
        var i = 0
        while (box == null && i < children.size) {
            b = children[i]
            if (b.type == type) box = b
            i++
        }
        return box
    }

    fun getChildren(): List<net.sourceforge.jaad.mp4.boxes.Box> {
        return children.toList()
    }

    override fun getChildren(type: Long): List<net.sourceforge.jaad.mp4.boxes.Box> {
        val l: MutableList<net.sourceforge.jaad.mp4.boxes.Box> =
            ArrayList<net.sourceforge.jaad.mp4.boxes.Box>()
        for (box in children) {
            if (box.type == type) l.add(box)
        }
        return l
    }

    @Throws(java.io.IOException::class)
    fun readChildren(`in`: MP4InputStream) {
        var box: net.sourceforge.jaad.mp4.boxes.Box
        while (`in`.getOffset() < offset + size) {
            box = net.sourceforge.jaad.mp4.boxes.BoxFactory.parseBox(this, `in`)
            children.add(box)
        }
    }

    @Throws(java.io.IOException::class)
    protected fun readChildren(`in`: MP4InputStream?, len: Int) {
        var box: net.sourceforge.jaad.mp4.boxes.Box
        for (i in 0 until len) {
            box = net.sourceforge.jaad.mp4.boxes.BoxFactory.parseBox(this, `in`!!)
            children.add(box)
        }
    }
}
