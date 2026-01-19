package it.univaq.kebapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = KebabOrange,
    onPrimary = KebabCream,
    primaryContainer = KebabOrangeDark,
    onPrimaryContainer = KebabCream,

    secondary = KebabYellow,
    onSecondary = KebabBrown,
    secondaryContainer = KebabCream,
    onSecondaryContainer = KebabBrown,

    tertiary = KebabRed,
    onTertiary = KebabCream,

    background = KebabSurface,
    onBackground = KebabOnSurface,
    surface = KebabSurface,
    onSurface = KebabOnSurface,

    error = KebabError,
    onError = KebabOnError
)

private val DarkColors = darkColorScheme(
    primary = KebabOrange,
    onPrimary = KebabBrown,
    primaryContainer = KebabBrown,
    onPrimaryContainer = KebabCream,

    secondary = KebabYellow,
    onSecondary = KebabBrown,
    secondaryContainer = KebabBrown,
    onSecondaryContainer = KebabYellow,

    tertiary = KebabRed,
    onTertiary = KebabCream,

    background = Color(0xFF121212),
    onBackground = Color(0xFFECECEC),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFECECEC),

    error = KebabError,
    onError = KebabOnError
)

@Composable
fun KebabbariApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
