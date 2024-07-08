package dev.uninit.mewsic.utils.weakmap

import dev.uninit.mewsic.utils.platform.WeakRef

/**
 * Mutable Map implementation with WeakRef values.
 */
class WeakValuesMap<K, V : Any> : WeakMapBase<K, V> {
    override val backed = mutableMapOf<K, WeakRef<V>>()

    override fun reap() {
        for ((k, v) in backed.entries.toList()) {
            if (v.isDead()) {
                backed.remove(k)
            }
        }
    }

    override fun remove(key: K): V? {
        reap()
        return backed.remove(key)?.get()
    }

    override fun putAll(from: Map<out K, V>) {
        for ((k, v) in from.entries) {
            put(k, v)
        }
    }

    override fun put(key: K, value: V): V? {
        return backed.put(key, WeakRef(value))?.get()
    }

    override fun get(key: K): V? {
        reap()
        for ((k, v) in backed.entries.toList()) {
            if (k == key) {
                return v.get()
            }
        }
        return null
    }

    override fun containsValue(value: V): Boolean {
        reap()
        for ((k, v) in backed.entries.toList()) {
            if (v == value) {
                return true
            }
        }
        return false
    }

    override fun containsKey(key: K): Boolean {
        reap()
        return backed.containsKey(key)
    }
}
