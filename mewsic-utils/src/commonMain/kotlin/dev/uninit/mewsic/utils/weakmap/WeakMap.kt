package dev.uninit.mewsic.utils.weakmap

import dev.uninit.mewsic.utils.platform.WeakRef

/**
 * Mutable Map implementation with WeakRef keys.
 */
class WeakMap<K : Any, V> : WeakMapBase<K, V> {
    override val backed = mutableMapOf<WeakRef<K>, V>()

    override fun reap() {
        for ((k, v) in backed.entries.toList()) {
            if (k.isDead()) {
                backed.remove(k)
            }
        }
    }

    override fun remove(key: K): V? {
        reap()
        for ((k, v) in backed.entries.toList()) {
            if (k == key) {
                return backed.remove(k)
            }
        }
        return null
    }

    override fun putAll(from: Map<out K, V>) {
        for ((k, v) in from.entries) {
            put(k, v)
        }
    }

    override fun put(key: K, value: V): V? {
        return backed.put(WeakRef(key), value)
    }

    override fun get(key: K): V? {
        reap()
        for ((k, v) in backed.entries.toList()) {
            if (k == key) {
                return v
            }
        }
        return null
    }

    override fun containsValue(value: V): Boolean {
        reap()
        return backed.containsValue(value)
    }

    override fun containsKey(key: K): Boolean {
        reap()
        for ((k, v) in backed.entries.toList()) {
            if (k == key) {
                return true
            }
        }
        return false
    }
}
