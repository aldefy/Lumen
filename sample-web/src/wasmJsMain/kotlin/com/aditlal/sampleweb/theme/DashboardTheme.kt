package com.aditlal.sampleweb.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DashboardColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color.White,
    secondary = Color(0xFF6366F1),
    onSecondary = Color.White,
    background = Color(0xFF09090B),
    onBackground = Color(0xFFE4E4E7),
    surface = Color(0xFF12121A),
    onSurface = Color(0xFFE4E4E7),
    surfaceVariant = Color(0xFF1A1A24),
    onSurfaceVariant = Color(0xFFA1A1AA),
    outline = Color(0xFF27272A),
    outlineVariant = Color(0xFF1E1E26),
)

@Composable
fun DashboardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DashboardColorScheme,
        content = content,
    )
}
