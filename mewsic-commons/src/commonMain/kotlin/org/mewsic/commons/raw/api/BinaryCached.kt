package org.mewsic.commons.raw.api

import kotlin.jvm.JvmStatic

interface BinaryCached {
    val id: ByteArray
    fun encode(): ByteArray
    fun decode(data: ByteArray): BinaryCached


}
