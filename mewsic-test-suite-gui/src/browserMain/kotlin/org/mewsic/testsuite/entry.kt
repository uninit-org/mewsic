package org.mewsic.testsuite

import org.jetbrains.compose.web.renderComposable

fun initialize(rootElementId: String) {
    renderComposable(rootElementId) {
        App()
    }
}
