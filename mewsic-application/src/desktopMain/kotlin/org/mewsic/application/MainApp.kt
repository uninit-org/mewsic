package org.mewsic.application

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.singleWindowApplication

fun main() = singleWindowApplication(
    title = "Mewsic Client",
) {
    App()
}

@Preview
@Composable
fun MainAppPreview() {
    App()
}
