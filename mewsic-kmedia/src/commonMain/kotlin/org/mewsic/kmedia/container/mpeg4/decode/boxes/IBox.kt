package org.mewsic.kmedia.container.mpeg4.decode.boxes

interface IBox {
    /**
     * Size of this box, including header
     */
    val size: Long
    /**
     * Type of this box as [char; 4] -> [u8; 4] -> UInt (u32)
     */
    val type: UInt
    /**
     * Offset of this box in the stream/file. This is needed as a seek point for random access.
     */
    val offset: Long
    /**
     * human-readable representation of the box's name
     */
    val name: String
    /**
     * Indicates if this box has children.
     */
    fun hasChildren(): Boolean
    /**
     * Indicated if the box has a child with the given type.
     */
    fun hasChild(type: UInt): Boolean
    /**
     * Returns an ordered and unmodifiable list of all direct children of this box. The list does not contain the children's children.
     */
    val children: List<IBox>?
    /**
     * Returns an ordered and unmodifiable list of all direct children of this box with the given type. The list does not contain the children's children.
     */
    fun getChildren(type: UInt): List<IBox>?
    /**
     * Returns the first child of this box with the given type. The child's children are not searched.
     */
    fun getChild(type: UInt): IBox?

}
