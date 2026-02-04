package io.luminos

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Colors used by the coachmark overlay and tooltip.
 */
@Immutable
data class CoachmarkColors(
    /** Background color for the scrim overlay */
    val scrimColor: Color = Color.Black.copy(alpha = 0.85f),
    /** Stroke color for the cutout border */
    val strokeColor: Color = Color.White,
    /** Color for the connector line from cutout to tooltip */
    val connectorColor: Color = Color.White,
    /** Background color for the tooltip card */
    val tooltipBackground: Color = Color.White,
    /** Background color for the tooltip card when showCard is enabled */
    val tooltipCardColor: Color = Color(0xFF2A2A2A).copy(alpha = 0.9f),
    /** Text color for the tooltip title */
    val titleColor: Color = Color.Black,
    /** Text color for the tooltip description */
    val descriptionColor: Color = Color(0xFF666666),
    /** Background color for the CTA button */
    val ctaButtonColor: Color = Color(0xFF007AFF),
    /** Text color for the CTA button */
    val ctaTextColor: Color = Color.White,
    /** Color for active progress indicator dots */
    val progressActiveColor: Color = Color(0xFF007AFF),
    /** Color for inactive progress indicator dots */
    val progressInactiveColor: Color = Color(0xFFE0E0E0),
)

/**
 * Light theme colors for coachmarks.
 * Scrim at 85% opacity for better text readability over content.
 */
val LightCoachmarkColors =
    CoachmarkColors(
        scrimColor = Color.Black.copy(alpha = 0.85f),
        strokeColor = Color.White,
        connectorColor = Color.White,
        tooltipBackground = Color.White,
        tooltipCardColor = Color(0xFF2A2A2A).copy(alpha = 0.9f),
        titleColor = Color.Black,
        descriptionColor = Color(0xFF666666),
        ctaButtonColor = Color(0xFF007AFF),
        ctaTextColor = Color.White,
        progressActiveColor = Color(0xFF007AFF),
        progressInactiveColor = Color(0xFFE0E0E0),
    )

/**
 * Dark theme colors for coachmarks.
 * Scrim at 88% opacity for better text readability over content.
 */
val DarkCoachmarkColors =
    CoachmarkColors(
        scrimColor = Color.Black.copy(alpha = 0.88f),
        strokeColor = Color.White,
        connectorColor = Color.White,
        tooltipBackground = Color(0xFF2A2A2A),
        tooltipCardColor = Color(0xFF1A1A1A).copy(alpha = 0.95f),
        titleColor = Color.White,
        descriptionColor = Color(0xFFCCCCCC),
        ctaButtonColor = Color(0xFF0A84FF),
        ctaTextColor = Color.White,
        progressActiveColor = Color(0xFF0A84FF),
        progressInactiveColor = Color(0xFF4A4A4A),
    )

/**
 * CompositionLocal for providing coachmark colors.
 */
val LocalCoachmarkColors = staticCompositionLocalOf { LightCoachmarkColors }

/**
 * Returns the appropriate coachmark colors based on the current theme.
 */
@Composable
fun coachmarkColors(darkTheme: Boolean = isSystemInDarkTheme()): CoachmarkColors {
    return if (darkTheme) DarkCoachmarkColors else LightCoachmarkColors
}
