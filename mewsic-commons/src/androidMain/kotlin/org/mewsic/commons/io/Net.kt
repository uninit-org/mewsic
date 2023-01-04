package org.mewsic.commons.io

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

actual val Dispatchers.Net: CoroutineContext
    get() = Dispatchers.IO
