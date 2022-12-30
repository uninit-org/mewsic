package net.sourceforge.jaad.mp4.boxes

interface Box {
    val parent: Box?

    /**
     * Returns the size of this box including its header.
     *
     * @return this box's size
     */
    val size: Long

    /**
     * Returns the type of this box as a 4CC converted to a long.
     *
     * @return this box's type
     */
    val type: Long

    /**
     * Returns the offset of this box in the stream/file. This is needed as a
     * seek point for random access.
     *
     * @return this box's offset
     */
    val offset: Long

    /**
     * Returns the name of this box as a human-readable string
     * (e.g. "Track Header Box").
     *
     * @return this box's name
     */
    val name: String?

    /**
     * Indicates if this box has children.
     *
     * @return true if this box contains at least one child
     */
    fun hasChildren(): Boolean

    /**
     * Indicated if the box has a child with the given type.
     *
     * @param type the type of child box to look for
     * @return true if this box contains at least one child with the given type
     */
    fun hasChild(type: Long): Boolean

    /**
     * Returns an ordered and unmodifiable list of all direct children of this
     * box. The list does not contain the children's children.
     *
     * @return this box's children
     */
    val children: List<Box>?

    /**
     * Returns an ordered and unmodifiable list of all direct children of this
     * box with the specified type. The list does not contain the children's
     * children. If there is no child with the given type, the list will be
     * empty.
     *
     * @param type the type of child boxes to look for
     * @return this box's children with the given type
     */
    fun getChildren(type: Long): List<Box>?

    /**
     * Returns the child box with the specified type. If this box has no child
     * with the given type, null is returned. To check if there is such a child
     * `hasChild(type)` can be used.
     * If more than one child exists with the same type, the first one will
     * always be returned. A list of all children with that type can be received
     * via `getChildren(type)`.
     *
     * @param type the type of child box to look for
     * @return the first child box with the given type, or null if none is found
     */
    fun getChild(type: Long): Box?
}
