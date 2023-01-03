package net.sourceforge.jaad.mp4.boxes

import net.sourceforge.jaad.mp4.MP4InputStream

open class BoxImpl(override val name: String) : Box {
    override var size: Long = 0
        protected set
    override var type: Long = 0
        protected set
    override var offset: Long = 0
        protected set
    override var parent: Box? = null
    override val children: MutableList<Box>

    init {
        children = ArrayList<Box>(4)
    }

    fun setParams(parent: Box?, size: Long, type: Long, offset: Long) {
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
    open fun decode(`in`: MP4InputStream) {
    }

    fun getParent(): Box? {
        return parent
    }

    override fun toString(): String {
        return name + " [" + BoxFactory.typeToString(type) + "]"
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

    override fun getChild(type: Long): Box? {
        var box: Box? = null
        var b: Box? = null
        var i = 0
        while (box == null && i < children.size) {
            b = children[i]
            if (b.type == type) box = b
            i++
        }
        return box
    }

    fun getChildren(): List<Box> {
        return children.toList()
    }

    override fun getChildren(type: Long): List<Box> {
        val l: MutableList<Box> =
            ArrayList<Box>()
        for (box in children) {
            if (box.type == type) l.add(box)
        }
        return l
    }

    @Throws(Exception::class)
    fun readChildren(`in`: MP4InputStream) {
        var box: Box
        while (`in`.getOffset() < offset + size) {
            box = BoxFactory.parseBox(this, `in`)
            children.add(box)
        }
    }

    @Throws(Exception::class)
    protected fun readChildren(`in`: MP4InputStream?, len: Int) {
        var box: Box
        for (i in 0 until len) {
            box = BoxFactory.parseBox(this, `in`!!)
            children.add(box)
        }
    }
}
