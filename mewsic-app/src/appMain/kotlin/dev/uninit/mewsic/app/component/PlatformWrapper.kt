package dev.uninit.mewsic.app.component

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformWrapper(content: @Composable () -> Unit)
