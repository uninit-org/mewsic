package org.mewsic.jaad.mp4.boxes.impl

import org.mewsic.jaad.mp4.MP4InputStream
import org.mewsic.jaad.mp4.boxes.FullBox

//TODO: check decoding, add get-methods
class ColorParameterBox : FullBox("Color Parameter Box") {
    private var colorParameterType: Long = 0
    private var primariesIndex = 0
    private var transferFunctionIndex = 0
    private var matrixIndex = 0

    @Throws(Exception::class)
    override fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        colorParameterType = `in`.readBytes(4)
        primariesIndex = `in`.readBytes(2).toInt()
        transferFunctionIndex = `in`.readBytes(2).toInt()
        matrixIndex = `in`.readBytes(2).toInt()
    }
}
