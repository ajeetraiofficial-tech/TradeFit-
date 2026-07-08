package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val CustomShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(24.dp),     // 24.dp rounded corners for standard cards
    large = RoundedCornerShape(28.dp),      // 28.dp rounded corners for larger containers
    extraLarge = RoundedCornerShape(32.dp)
)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = PureWhite,
    primaryContainer = DeepSlateBlue,
    onPrimaryContainer = LightBlueAccent,
    secondary = ExecutiveBlue,
    onSecondary = PureWhite,
    background = MatteBlack,
    onBackground = OffWhite,
    surface = Color(0xFF111827), // Deep grey/black slate
    onSurface = OffWhite,
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = OffWhite,
    outline = SlateTextDark,
    error = WarningAlert
)

private val LightColorScheme = lightColorScheme(
    primary = ExecutiveBlue,
    onPrimary = PureWhite,
    primaryContainer = LightBlueAccent,
    onPrimaryContainer = DeepSlateBlue,
    secondary = ElectricBlue,
    onSecondary = PureWhite,
    background = OffWhite,
    onBackground = MatteBlack,
    surface = PureWhite,
    onSurface = MatteBlack,
    surfaceVariant = OffWhite,
    onSurfaceVariant = SlateTextDark,
    outline = DarkSlateBorder,
    error = WarningAlert
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false by default to preserve TradeFit ERP's premium blue/black/white branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = CustomShapes,
        typography = Typography,
        content = content
    )
}
