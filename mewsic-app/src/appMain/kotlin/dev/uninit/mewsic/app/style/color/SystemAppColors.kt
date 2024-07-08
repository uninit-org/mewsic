package dev.uninit.mewsic.app.style.color

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import dev.uninit.mewsic.api.style.color.AppColors


@Composable
expect fun SystemAppColors(darkTheme: Boolean = isSystemInDarkTheme()): AppColors
