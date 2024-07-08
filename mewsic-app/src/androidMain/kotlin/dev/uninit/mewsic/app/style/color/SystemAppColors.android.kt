package dev.uninit.mewsic.app.style.color

import android.os.Build
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.uninit.mewsic.api.style.color.AppColors
import dev.uninit.mewsic.api.style.color.DataAppColors
import dev.uninit.mewsic.api.style.color.LightDarkAppColors

@Composable
actual fun SystemAppColors(darkTheme: Boolean): AppColors {
    val (light, dark) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        dynamicLightColorScheme(context) to dynamicDarkColorScheme(context)
    } else {
        lightColorScheme() to darkColorScheme()
    }

    val colors = LightDarkAppColors(
        DataAppColors.fromScheme(light),
        DataAppColors.fromScheme(dark),
        isInDarkTheme = darkTheme
    )

    return colors
}
