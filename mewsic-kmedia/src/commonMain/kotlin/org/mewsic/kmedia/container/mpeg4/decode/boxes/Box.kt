package org.mewsic.kmedia.container.mpeg4.decode.boxes

import org.mewsic.commons.streams.api.InputStream
import org.mewsic.commons.streams.api.SeekableInputStream

class Box : IBox {

    constructor(from: InputStream, name: String, type: UInt, size: Long) {
        this.name = name
        this.offset = 0 // Without implementing Seekable, we can't get random access anyways
        this.size = size
        this.type = type
    }

    constructor(buffer: ByteArray, name: String, type: UInt, size: Long) {
        this.name = name
        this.offset = 0
        this.size = size
        this.type = type
    }

    constructor(seekable: SeekableInputStream, name: String, type: UInt, size: Long) {
        this.name = name
        this.offset = seekable.position()
        this.type = type
        this.size = size
    }


    /**
     * Size of this box, including header
     */
    override val size: Long

    /**
     * Type of this box as [char; 4] -> [u8; 4] -> UInt (u32)
     */
    override val type: UInt

    /**
     * Offset of this box in the stream/file. This is needed as a seek point for random access.
     */
    override val offset: Long

    /**
     * human-readable representation of the box's name
     */
    override val name: String


    /**
     * Indicates if this box has children.
     */
    override fun hasChildren(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Indicated if the box has a child with the given type.
     */
    override fun hasChild(type: UInt): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Returns an ordered and unmodifiable list of all direct children of this box. The list does not contain the children's children.
     */
    override val children: List<IBox>?
        get() = TODO("Not yet implemented")

    /**
     * Returns an ordered and unmodifiable list of all direct children of this box with the given type. The list does not contain the children's children.
     */
    override fun getChildren(type: UInt): List<IBox>? {
        TODO("Not yet implemented")
    }

    /**
     * Returns the first child of this box with the given type. The child's children are not searched.
     */
    override fun getChild(type: UInt): IBox? {
        TODO("Not yet implemented")
    }

}
