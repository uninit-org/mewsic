package org.mewsic.testsuite.core.media

import org.mewsic.commons.lang.Log
import org.mewsic.commons.sources.ByteArraySource
import org.mewsic.jaad.mp4.MP4Container
import org.mewsic.jaad.mp4.boxes.Box

class M4A(byteArray: ByteArray, var eventWatcher: EventWatcher? = null) {
    fun interface EventWatcher {
        fun onEvent(eventName: String, args: Map<String, Any>)
    }
    val source: ByteArraySource = ByteArraySource(byteArray)
    val mediaStream: MP4Container
    init {
        Log.info("Opening M4A, passing to MP4Container constructor")
        mediaStream = MP4Container(source.open())
        eventWatcher?.onEvent("OPEN", mapOf("message" to "M4A opened"))

    }
    fun doRead() {
        eventWatcher?.onEvent("READ", mapOf("message" to "Reading M4A"))
        mediaStream.readContent { eventName, args ->
            eventWatcher?.onEvent(eventName, args)
        }
    }

    fun brand(): Pair<String, String> {
        return Pair(mediaStream.majorBrand?.description ?: "NONE", mediaStream.minorBrand?.description ?: "NONE")
    }
    fun boxesNames(): List<String> {
        return mediaStream.getBoxes().map { it.name ?: "NONE" }
    }
    fun boxes(): List<Box> {
        return mediaStream.getBoxes()
    }




}
