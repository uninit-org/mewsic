package org.mewsic.testsuite

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.singleWindowApplication
import org.mewsic.commons.streams.toCommonInputStream
//import org.mewsic.mediaformat.m4a.reader.M4AReader
import java.io.File

fun main() = singleWindowApplication(
    title = "Mewsic Test Suite",
) {
    App()
}

@Preview
@Composable
fun MainAppPreview() {
    App()
}
