package com.phywarp.time.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme(val label: String, val primary: Color, val secondary: Color) {
    BLUE("简约蓝", BluePrimary, BlueSecondary),
    GREEN("护眼绿", GreenPrimary, GreenSecondary),
    WHITE("纯净白", Color(0xFFE0E0E0), Color(0xFFFFFFFF)),
    DARK("深灰黑", DarkPrimary, DarkSecondary),
    ORANGE("暖橘色", OrangePrimary, OrangeSecondary)
}

@Composable
fun TimePhyWarpTheme(
    appTheme: AppTheme = AppTheme.BLUE,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = appTheme.primary,
        secondary = appTheme.secondary,
        background = SurfaceDark,
        surface = SurfaceCard,
        onBackground = TextPrimary,
        onSurface = TextPrimary
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
