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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme && appTheme != AppTheme.WHITE) {
        darkColorScheme(
            primary = appTheme.primary,
            secondary = appTheme.secondary,
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        )
    } else {
        lightColorScheme(
            primary = appTheme.primary,
            secondary = appTheme.secondary,
            background = BackgroundLight,
            surface = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
