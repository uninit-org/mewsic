package dev.uninit.mewsic.app.style.color

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import dev.uninit.mewsic.api.style.color.AppColors
import dev.uninit.mewsic.api.style.color.DataAppColors
import dev.uninit.mewsic.api.style.color.LightDarkAppColors

@Composable
actual fun SystemAppColors(darkTheme: Boolean): AppColors {
    return LightDarkAppColors(
        DataAppColors.fromScheme(lightColorScheme()),
        DataAppColors.fromScheme(darkColorScheme()),
        isInDarkTheme = darkTheme
    )
}
