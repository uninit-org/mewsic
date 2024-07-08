package dev.uninit.mewsic.api.style.color

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Data")
data class DataAppColors(
    val background: ULong,
    val onBackground: ULong,
    val primary: ULong,
    val onPrimary: ULong,
    val secondary: ULong,
    val onSecondary: ULong,
) : AppColors {
    override fun background(): Color = Color(background)
    override fun onBackground(): Color = Color(onBackground)
    override fun primary(): Color = Color(primary)
    override fun onPrimary(): Color = Color(onPrimary)
    override fun secondary(): Color = Color(secondary)
    override fun onSecondary(): Color = Color(onSecondary)

    companion object {
        fun fromScheme(scheme: ColorScheme): AppColors {
            return DataAppColors(
                background = scheme.background.value,
                onBackground = scheme.onBackground.value,
                primary = scheme.primary.value,
                onPrimary = scheme.onPrimary.value,
                secondary = scheme.secondary.value,
                onSecondary = scheme.onSecondary.value
            )
        }
    }
}
