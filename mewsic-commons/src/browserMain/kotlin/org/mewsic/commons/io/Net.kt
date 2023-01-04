package org.mewsic.commons.io

import kotlinx.browser.window
import org.w3c.dom.Window
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*
actual val Dispatchers.Net : CoroutineContext
    get() = window.asCoroutineDispatcher()
