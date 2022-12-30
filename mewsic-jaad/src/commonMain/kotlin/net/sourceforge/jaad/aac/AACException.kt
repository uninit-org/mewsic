package net.sourceforge.jaad.aac
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.InputStream
/**
 * Standard exception, thrown when decoding of an AAC frame fails.
 * The message gives more detailed information about the error.
 * @author in-somnia
 */
class AACException : Exception {
    val isEndOfStream: Boolean

    constructor(message: String?, eos: Boolean = false) : super(message) {
        isEndOfStream = eos
    }

    constructor(cause: Throwable?) : super(cause) {
        isEndOfStream = false
    }
}
