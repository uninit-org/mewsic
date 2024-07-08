package dev.uninit.mewsic.api.style.color

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("LightDark")
data class LightDarkAppColors(
    val light: AppColors,
    val dark: AppColors,
    val isInDarkTheme: Boolean,
) : AppColors {
    override fun background(): Color = if (isInDarkTheme) dark.background() else light.background()
    override fun onBackground(): Color = if (isInDarkTheme) dark.onBackground() else light.onBackground()
    override fun primary(): Color = if (isInDarkTheme) dark.primary() else light.primary()
    override fun onPrimary(): Color = if (isInDarkTheme) dark.onPrimary() else light.onPrimary()
    override fun secondary(): Color = if (isInDarkTheme) dark.secondary() else light.secondary()
    override fun onSecondary(): Color = if (isInDarkTheme) dark.onSecondary() else light.onSecondary()
}
