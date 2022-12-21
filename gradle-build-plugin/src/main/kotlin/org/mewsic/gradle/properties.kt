package org.mewsic.gradle

import org.gradle.api.Project
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private class DefaultPropertyProvider<T>(private val project: Project, private val default: T) : ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (project.hasProperty(property.name)) {
            return project.property(property.name) as T
        }
        if (project.extensions.extraProperties.has(property.name)) {
            return project.extensions.extraProperties.get(property.name) as T
        }
        return default
    }
}

private class TransformingPropertyProvider<T>(private val project: Project, private val transform: (Any?) -> T) : ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (project.hasProperty(property.name)) {
            return transform(project.property(property.name))
        }
        if (project.extensions.extraProperties.has(property.name)) {
            return transform(project.extensions.extraProperties.get(property.name))
        }
        return transform(null)
    }
}

fun <T> Project.defaultProperty(default: T): ReadOnlyProperty<Any?, T> = DefaultPropertyProvider(this, default)
fun <T> Project.transformedProperty(check: (Any?) -> T): ReadOnlyProperty<Any?, T> = TransformingPropertyProvider(this, check)
