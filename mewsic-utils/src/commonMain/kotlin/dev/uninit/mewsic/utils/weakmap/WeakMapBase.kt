package dev.uninit.mewsic.utils.weakmap

/**
 * Base implementation for weakref maps
 */
interface WeakMapBase<K, V> : MutableMap<K, V> {
    val backed: MutableMap<*, *>
    fun reap()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = TODO("Not yet implemented")
    override val keys: MutableSet<K>
        get() = TODO("Not yet implemented")
    override val size: Int
        get() {
            reap()
            return backed.size
        }
    override val values: MutableCollection<V>
        get() = TODO("Not yet implemented")

    override fun clear() {
        backed.clear()
    }

    override fun isEmpty(): Boolean {
        reap()
        return backed.isEmpty()
    }
}
