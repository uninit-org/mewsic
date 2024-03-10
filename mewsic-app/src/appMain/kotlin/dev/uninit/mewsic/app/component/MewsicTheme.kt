package dev.uninit.mewsic.app.component

import androidx.compose.runtime.Composable

@Composable
expect fun MewsicTheme(
    isDarkTheme: Boolean,
    content: @Composable () -> Unit,
)
