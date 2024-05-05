package dev.uninit.mewsic.app.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import dev.uninit.mewsic.app.generated.Fredoka_VariableFont
import dev.uninit.mewsic.app.generated.Res
import org.jetbrains.compose.resources.Font

@Composable
actual fun MewsicTheme(
    isDarkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val colors = if (isDarkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    val fontFamily = FontFamily(Font(Res.font.Fredoka_VariableFont))

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(
            displayLarge = MaterialTheme.typography.displayLarge.copy(
                fontFamily = fontFamily
            ),
            displayMedium = MaterialTheme.typography.displayMedium.copy(
                fontFamily = fontFamily
            ),
            displaySmall = MaterialTheme.typography.displaySmall.copy(
                fontFamily = fontFamily
            ),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = fontFamily
            ),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = fontFamily
            ),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = fontFamily
            ),
            titleLarge = MaterialTheme.typography.titleLarge.copy(
                fontFamily = fontFamily
            ),
            titleMedium = MaterialTheme.typography.titleMedium.copy(
                fontFamily = fontFamily
            ),
            titleSmall = MaterialTheme.typography.titleSmall.copy(
                fontFamily = fontFamily
            ),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = fontFamily
            ),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = fontFamily
            ),
            bodySmall = MaterialTheme.typography.bodySmall.copy(
                fontFamily = fontFamily
            ),
            labelLarge = MaterialTheme.typography.labelLarge.copy(
                fontFamily = fontFamily
            ),
            labelMedium = MaterialTheme.typography.labelMedium.copy(
                fontFamily = fontFamily
            ),
            labelSmall = MaterialTheme.typography.labelSmall.copy(
                fontFamily = fontFamily
            ),
        ),
        content = content
    )
}
