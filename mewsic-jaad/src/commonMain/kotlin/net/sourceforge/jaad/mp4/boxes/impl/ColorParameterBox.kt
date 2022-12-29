package net.sourceforge.jaad.mp4.boxes.impl

import net.sourceforge.jaad.mp4.MP4InputStream

//TODO: check decoding, add get-methods
class ColorParameterBox : FullBox("Color Parameter Box") {
    private var colorParameterType: Long = 0
    private var primariesIndex = 0
    private var transferFunctionIndex = 0
    private var matrixIndex = 0
    @Throws(java.io.IOException::class)
    fun decode(`in`: MP4InputStream) {
        super.decode(`in`)
        colorParameterType = `in`.readBytes(4)
        primariesIndex = `in`.readBytes(2) as Int
        transferFunctionIndex = `in`.readBytes(2) as Int
        matrixIndex = `in`.readBytes(2) as Int
    }
}
