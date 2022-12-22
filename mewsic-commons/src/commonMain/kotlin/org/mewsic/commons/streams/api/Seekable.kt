package org.mewsic.commons.streams.api

interface Seekable {
    fun seek(offset: Long)
    fun position(): Long
    fun length(): Long
}