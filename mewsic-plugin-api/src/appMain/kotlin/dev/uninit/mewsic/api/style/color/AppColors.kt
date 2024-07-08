package dev.uninit.mewsic.api.style.color

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppColors {
    fun background(): Color
    fun onBackground(): Color

    fun primary(): Color
    fun onPrimary(): Color

    fun secondary(): Color
    fun onSecondary(): Color
}

val LocalAppColors = staticCompositionLocalOf<AppColors> { error("No colors provided") }
