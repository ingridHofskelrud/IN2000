package no.uio.ifi.in2000.team33.smaafly.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4D7296),
    onPrimary = Color(0xFFD2E3F1),
    secondary = Color(0xFF4D678B),
    onSecondary = Color(0xFFD2E1F1),
    tertiary = Color(0xFF006783),
    onTertiary = Color(0xFF1A1B1E),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E1E6),
    surface = Color(0xFF1F1F1F),
    onSurface = Color(0xFFE1E1E6),
    primaryContainer = Color(0xFF375070),
    onPrimaryContainer = Color(0xFFD2E3F1),
    secondaryContainer = Color(0xFF3E506D),
    onSecondaryContainer = Color(0xFFD2E1F1),
    tertiaryContainer = Color(0xFF5A6AB8),
    onTertiaryContainer = Color(0xFF1A1B1E),
    error = Color(0xFFCF6679),
    onError = Color(0xFF1E1E1E)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006492),
    secondary = Color(0xFF00658B),
    tertiary = Color(0xFF006783),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFDFBFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF001B3D),
    onSurface = Color(0xFF001B3D),
    primaryContainer = Color(0xFFcae6ff),
    onPrimaryContainer = Color(0xFF001e2f),
    secondaryContainer = Color(0xFFc4e7ff),
    onSecondaryContainer = Color(0xFF001e2c),
    tertiaryContainer = Color(0xFFbde9ff),
    onTertiaryContainer = Color(0xFF001f2a)
)

@Composable
fun SmaaflyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}