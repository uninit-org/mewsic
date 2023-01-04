package org.mewsic.testsuite.platform

import androidx.compose.runtime.Composable

actual fun showToast(message: String) {
    js(
        """
        alert(message);
        """
    )
}
