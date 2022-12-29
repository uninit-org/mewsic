package externals

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget

external interface webkitAudioContext : AudioContext
external interface AudioContext {
    val sampleRate: Float
    val destination: AudioNode
    fun createGain(): GainNode
    fun createBufferSource(): AudioBufferSourceNode
    fun createBuffer(channels: Int, length: Int, sampleRate: Float): AudioBuffer
}


fun getAudioContext(): AudioContext {
    return js("new (window.AudioContext || window.webkitAudioContext)()").unsafeCast<AudioContext>()
}

external interface AudioNode {
    fun connect(destination: AudioNode)
    fun connect(destination: AudioParam)

    fun addEventListener(type: String, callback: EventListener?, options: dynamic = definedExternally)
    fun addEventListener(type: String, callback: ((Event) -> Unit)?, options: dynamic = definedExternally)
    fun removeEventListener(type: String, callback: EventListener?, options: dynamic = definedExternally)
    fun removeEventListener(type: String, callback: ((Event) -> Unit)?, options: dynamic = definedExternally)
    fun dispatchEvent(event: Event): Boolean
}

external interface GainNode : AudioNode {
    val gain: AudioParam
}

external interface AudioParam {
    var value: Float
    val defaultValue: Float
    val minValue: Float
    val maxValue: Float
}

external interface AudioBufferSourceNode : AudioNode {
    var buffer: AudioBuffer?
    var loop: Boolean
    var loopStart: Float
    var loopEnd: Float
    var onended: (() -> Unit)?
    fun start(`when`: Float = definedExternally, offset: Float = definedExternally, duration: Float = definedExternally)
    fun stop(`when`: Float = definedExternally)
}

external interface AudioBuffer {
    val sampleRate: Float
    val length: Int
    val duration: Float
    val numberOfChannels: Int
    fun getChannelData(channel: Int): FloatArray
}
