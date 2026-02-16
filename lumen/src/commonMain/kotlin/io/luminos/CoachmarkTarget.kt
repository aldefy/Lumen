package io.luminos

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Defines the shape of the cutout around a coachmark target.
 */
sealed interface CutoutShape {
    /**
     * Circular cutout, ideal for round buttons or icons.
     *
     * @param radius Explicit radius for the circle. When specified (not [Dp.Unspecified]),
     *               this value is used as the base radius, ignoring the target bounds size.
     *               When [Dp.Unspecified] (default), radius is auto-calculated as
     *               `max(bounds.width, bounds.height) / 2`.
     * @param radiusPadding Additional padding added to the radius (whether explicit or auto-calculated).
     */
    data class Circle(
        val radius: Dp = Dp.Unspecified,
        val radiusPadding: Dp = 8.dp,
    ) : CutoutShape

    /**
     * Rounded rectangle cutout with customizable corner radius.
     */
    data class RoundedRect(
        val cornerRadius: Dp = 12.dp,
        val padding: Dp = 8.dp,
    ) : CutoutShape

    /**
     * Rectangular cutout with no corner radius.
     */
    data class Rect(val padding: Dp = 8.dp) : CutoutShape

    /**
     * iOS-style squircle (superellipse) cutout with smooth corners.
     */
    data class Squircle(
        val cornerRadius: Dp = 20.dp,
        val padding: Dp = 8.dp,
    ) : CutoutShape

    /**
     * Star-shaped cutout, fun for gamification or achievement highlights.
     *
     * @param points Number of points on the star (default 5).
     * @param innerRadiusRatio Ratio of inner radius to outer radius (0.0-1.0, default 0.5).
     * @param padding Additional padding around the star.
     */
    data class Star(
        val points: Int = 5,
        val innerRadiusRatio: Float = 0.5f,
        val padding: Dp = 8.dp,
    ) : CutoutShape
}

/**
 * Specifies where the tooltip should appear relative to the target.
 */
enum class TooltipPosition {
    TOP,
    BOTTOM,
    START,
    END,

    /**
     * Automatically determine the best position based on available space.
     */
    AUTO,
}

/**
 * Animation style for the highlighted cutout area.
 * Pulse animations improve feature adoption by drawing attention to the target.
 *
 * Can be set globally via [CoachmarkConfig.highlightAnimation] or per-target
 * via [CoachmarkTarget.highlightAnimation] (per-target overrides global).
 *
 * @see CoachmarkConfig.pulseDurationMs for controlling animation speed
 */
enum class HighlightAnimation {
    /** No animation - static cutout stroke (default) */
    NONE,

    /**
     * Gentle breathing/scaling effect on the cutout stroke.
     *
     * Animation: stroke scales 1.0 -> 1.08 -> 1.0 continuously.
     * The stroke expands slightly outward from the cutout center, creating
     * a subtle "breathing" effect that draws attention without being distracting.
     *
     * Duration controlled by [CoachmarkConfig.pulseDurationMs] (default 1000ms for full cycle).
     */
    PULSE,

    /**
     * Animated glow effect with pulsing stroke width and alpha.
     *
     * Animation:
     * - Stroke width: 1x -> 2x -> 1x (thickens and thins)
     * - Stroke alpha: 1.0 -> 0.5 -> 1.0 (fades and brightens)
     *
     * Creates a more prominent "glowing" effect compared to [PULSE].
     * Best for high-priority targets that need maximum attention.
     *
     * Duration controlled by [CoachmarkConfig.pulseDurationMs] (default 1000ms for full cycle).
     */
    GLOW,

    /**
     * Expanding ripple rings that emanate outward from the target.
     *
     * Animation: Multiple concentric rings expand outward while fading,
     * creating a "ripple in water" effect that draws the eye inward.
     *
     * Duration controlled by [CoachmarkConfig.pulseDurationMs] (default 1000ms per ripple cycle).
     */
    RIPPLE,

    /**
     * A shimmer/shine effect that sweeps around the stroke.
     *
     * Animation: A bright highlight travels along the stroke perimeter,
     * similar to a loading indicator or "shine" effect on buttons.
     *
     * Duration controlled by [CoachmarkConfig.pulseDurationMs] (default 1000ms for full sweep).
     */
    SHIMMER,

    /**
     * Bouncy scale animation with overshoot.
     *
     * Animation: The stroke quickly scales up with overshoot then settles back,
     * creating an energetic "pop" effect. Repeats periodically.
     *
     * Duration controlled by [CoachmarkConfig.pulseDurationMs] (default 1000ms per bounce cycle).
     */
    BOUNCE,
}

/**
 * Specifies the style/angle of the connector line from cutout to tooltip.
 */
enum class ConnectorStyle {
    /**
     * Automatically determine the best connector style based on tooltip position.
     * - Tooltip above/below target -> VERTICAL
     * - Tooltip left/right of target -> HORIZONTAL
     */
    AUTO,

    /**
     * Line goes directly from cutout edge to tooltip (diagonal).
     */
    DIRECT,

    /**
     * Line goes horizontally from cutout edge, dot at the end.
     */
    HORIZONTAL,

    /**
     * Line goes vertically from cutout edge, dot at the end.
     */
    VERTICAL,

    /**
     * L-shaped connector: horizontal from cutout edge, then 90 degree turn down/up to tooltip.
     */
    ELBOW,

    /** Smooth curved connector using a quadratic Bezier curve. */
    CURVED,
}

/**
 * Defines what happens when user taps on the coachmark target cutout area.
 *
 * Note: The scrim overlay intercepts all taps. True pass-through to the underlying UI
 * is not possible. Use the [CoachmarkHost.onTargetTap] callback to programmatically
 * trigger the target's action when needed.
 */
enum class TargetTapBehavior {
    /** Default — tap on cutout does nothing; only the CTA button advances. */
    PASS_THROUGH,
    /** Tap on cutout area advances to the next step or dismisses. */
    ADVANCE,
    /** Tap on cutout fires [CoachmarkHost.onTargetTap] callback AND advances. */
    BOTH,
}

/** Visual style at the endpoint of the connector line. */
enum class ConnectorEndStyle {
    /** Small filled circle (current default). */
    DOT,
    /** Directional arrowhead pointing toward the tooltip. */
    ARROW,
    /** No endpoint decoration. */
    NONE,
    /** Custom rendering via CoachmarkConfig.customConnectorEnd lambda. */
    CUSTOM,
}

/**
 * Represents a single coachmark target element.
 *
 * @property id Unique identifier for this target, used for persistence
 * @property bounds The screen coordinates of the target element
 * @property shape The cutout shape to use around the target
 * @property title The headline text for the tooltip
 * @property description The body text for the tooltip
 * @property tooltipPosition Where to position the tooltip relative to the target
 * @property connectorStyle The style/angle of the connector line
 * @property connectorLength Length of connector line (Dp.Unspecified = auto/default 40dp)
 * @property connectorEndStyle Visual style at the endpoint of the connector line
 * @property ctaText Custom CTA button text (defaults to "Got it!")
 * @property showProgressIndicator Override for progress indicator visibility.
 *           - `null` (default): Use global [CoachmarkConfig.showProgressIndicator]
 *           - `true`: Always show progress indicator for this target
 *           - `false`: Always hide progress indicator for this target
 * @property highlightAnimation Animation style for the cutout highlight.
 *           - `null` (default): Use global [CoachmarkConfig.highlightAnimation]
 *           - Explicit value: Override for this specific target
 * @property targetTapBehavior What happens when user taps the cutout area.
 *           [TargetTapBehavior.PASS_THROUGH] (default) — tap does nothing.
 *           [TargetTapBehavior.ADVANCE] — tap advances/dismisses.
 *           [TargetTapBehavior.BOTH] — fires onTargetTap callback and advances.
 */
@Immutable
data class CoachmarkTarget(
    val id: String,
    val bounds: Rect = Rect.Zero,
    val shape: CutoutShape = CutoutShape.Circle(),
    val title: String,
    val description: String,
    val tooltipPosition: TooltipPosition = TooltipPosition.AUTO,
    val connectorStyle: ConnectorStyle = ConnectorStyle.AUTO,
    val connectorLength: Dp = Dp.Unspecified,
    val connectorEndStyle: ConnectorEndStyle = ConnectorEndStyle.DOT,
    val ctaText: String = "Got it!",
    val showProgressIndicator: Boolean? = null,
    val highlightAnimation: HighlightAnimation? = null,
    val targetTapBehavior: TargetTapBehavior = TargetTapBehavior.PASS_THROUGH,
)
