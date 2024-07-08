@file:OptIn(ExperimentalNativeApi::class)

package dev.uninit.mewsic.utils.platform

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference

actual class WeakRef<T : Any> actual constructor(value: T) {
    private val impl = WeakReference(value)
    actual fun get(): T? = impl.get()
    actual fun isDead() = impl.get() == null

    override fun equals(other: Any?): Boolean {
        if (other is WeakRef<*>) {
            if (this === other) {
                return true
            }

            val backedValue = impl.get()
            val otherValue = other.impl.get()
            return otherValue == backedValue
        } else {
            return other == impl.get()
        }
    }
}
