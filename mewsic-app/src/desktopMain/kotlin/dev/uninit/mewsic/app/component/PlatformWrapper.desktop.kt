package dev.uninit.mewsic.app.component

import androidx.compose.runtime.Composable

@Composable
actual inline fun PlatformWrapper(content: @Composable () -> Unit) = content()
