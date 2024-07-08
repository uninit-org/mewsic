package dev.uninit.mewsic.utils.platform

expect class WeakRef<T : Any>(value: T) {
    fun isDead(): Boolean
    fun get(): T?
}
