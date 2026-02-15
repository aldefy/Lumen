package io.luminos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import io.luminos.shapes.createPaddedSquirclePath
import io.luminos.shapes.createPaddedStarPath
import io.luminos.shapes.createScaledStarPath
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.roundToInt

/**
 * Host composable that wraps content and renders coachmark scrim on top.
 *
 * This is the recommended way to integrate coachmarks - it handles the scrim
 * positioning internally so clients don't need to restructure their UI.
 *
 * Example usage:
 * ```
 * val coachmarkController = rememberCoachmarkController()
 *
 * CoachmarkHost(controller = coachmarkController) {
 *     Scaffold(...) { ... }
 * }
 * ```
 *
 * @param controller The [CoachmarkController] managing coachmark state
 * @param config Configuration for scrim appearance
 * @param colors Theme colors for the scrim
 * @param onDismiss Callback when the scrim is dismissed
 * @param onStepCompleted Callback when a step is completed
 * @param content The content to render underneath the coachmark scrim
 */
@Composable
fun CoachmarkHost(
    controller: CoachmarkController,
    modifier: Modifier = Modifier,
    config: CoachmarkConfig = CoachmarkConfig(),
    colors: CoachmarkColors = coachmarkColors(),
    onDismiss: () -> Unit = {},
    onStepCompleted: (stepIndex: Int, targetId: String) -> Unit = { _, _ -> },
    content: @Composable () -> Unit,
) {
    // Auto-dismiss coachmark when a dialog appears
    val coordinator = LocalOverlayCoordinator.current
    val dialogCount by coordinator?.activeDialogCount?.collectAsState()
        ?: remember { mutableStateOf(0) }
    val coachmarkState by controller.state.collectAsState()

    LaunchedEffect(dialogCount) {
        if (dialogCount > 0 && coachmarkState !is CoachmarkState.Hidden) {
            controller.dismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                // Track viewport bounds for visibility checking
                controller.setViewportBounds(
                    Rect(
                        offset = Offset.Zero,
                        size = Size(
                            coordinates.size.width.toFloat(),
                            coordinates.size.height.toFloat()
                        )
                    )
                )
            }
    ) {
        content()

        CoachmarkScrim(
            controller = controller,
            config = config,
            colors = colors,
            onDismiss = onDismiss,
            onStepCompleted = onStepCompleted,
        )
    }
}

/**
 * Configuration for the coachmark scrim appearance.
 */
data class CoachmarkConfig(
    /** Stroke width for the cutout border */
    val strokeWidth: Dp = 2.dp,
    /** Radius of the dot at the end of the connector line */
    val connectorDotRadius: Dp = 4.dp,
    /** Minimum distance from screen edges for the tooltip */
    val tooltipMargin: Dp = 16.dp,
    /** Gap between the cutout and the tooltip */
    val tooltipGap: Dp = 16.dp,
    /** Corner radius for the tooltip card */
    val tooltipCornerRadius: Dp = 16.dp,
    /** Gap between connector endpoint and tooltip edge */
    val connectorTooltipGap: Dp = 8.dp,
    /** Duration of fade animations in milliseconds */
    val fadeAnimationDuration: Int = 300,
    /** Duration of connector line animation in milliseconds */
    val connectorAnimationDuration: Int = 200,
    /** Duration of tooltip slide animation in milliseconds */
    val tooltipAnimationDuration: Int = 250,
    /** Scrim opacity level */
    val scrimOpacity: ScrimOpacity? = null,
    /** Behavior when user taps on the scrim (outside the cutout) */
    val scrimTapBehavior: ScrimTapBehavior = ScrimTapBehavior.DISMISS,
    /** Whether to show progress indicator dots for multi-step sequences */
    val showProgressIndicator: Boolean = true,
    /** Whether to wrap the tooltip content in a card/box */
    val showTooltipCard: Boolean = false,
    /** Behavior when user presses the back button */
    val backPressBehavior: BackPressBehavior = BackPressBehavior.DISMISS,
    /** Default animation for the cutout highlight */
    val highlightAnimation: HighlightAnimation = HighlightAnimation.NONE,
    /** Duration of one pulse cycle in milliseconds */
    val pulseDurationMs: Int = 1000,
    /** Whether to show a "Skip" button */
    val showSkipButton: Boolean = false,
    /** Text for the skip button */
    val skipButtonText: String = "Skip",
    /** Delay in milliseconds before the coachmark appears */
    val delayBeforeShow: Long = 0L,
    /** Wait for target to be visible in viewport before showing (for LazyColumn support) */
    val waitForVisibility: Boolean = true,
    /** Delay in milliseconds after scroll stops before showing coachmark */
    val visibilityDelay: Long = 150L,
    /** Timeout in ms after auto-scroll to wait for target visibility. If still not visible, skip. */
    val scrollTimeout: Long = 2000L,
)

/**
 * Defines what happens when user taps the scrim (outside the cutout).
 */
enum class ScrimTapBehavior {
    /** Dismisses the entire coachmark overlay/sequence */
    DISMISS,
    /** Advances to next step (same as clicking CTA) */
    ADVANCE,
    /** Does nothing - user must tap the CTA button */
    NONE,
}

/**
 * Defines what happens when user presses the back button during a coachmark.
 */
enum class BackPressBehavior {
    /** Always dismisses the entire coachmark/sequence (default) */
    DISMISS,
    /** Navigate back through the sequence: go to previous step, dismiss on first step */
    NAVIGATE,
}

/**
 * Predefined scrim opacity levels.
 */
enum class ScrimOpacity(val alpha: Float) {
    /** Light overlay (30% black) */
    LIGHT(0.30f),
    /** Medium overlay (50% black) */
    MEDIUM(0.50f),
    /** Dark overlay (70% black) */
    DARK(0.70f),
    /** Extra dark overlay (85% black) - default */
    EXTRA_DARK(0.85f),
}

/**
 * Full-screen coachmark scrim that displays educational tooltips.
 */
@Composable
fun CoachmarkScrim(
    controller: CoachmarkController,
    modifier: Modifier = Modifier,
    config: CoachmarkConfig = CoachmarkConfig(),
    colors: CoachmarkColors = coachmarkColors(),
    onDismiss: () -> Unit = {},
    onStepCompleted: (stepIndex: Int, targetId: String) -> Unit = { _, _ -> },
) {
    val state by controller.state.collectAsState()

    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val currentOnStepCompleted by rememberUpdatedState(onStepCompleted)

    // Track visibility state with delay for smooth appearance after scroll stops
    var isReadyToShow by remember { mutableStateOf(false) }
    val isScrolling = controller.isScrolling

    // Get current target ID for visibility check
    val currentTargetId = when (val s = state) {
        is CoachmarkState.Showing -> s.target.id
        is CoachmarkState.Sequence -> s.currentTarget.id
        CoachmarkState.Hidden -> null
    }

    // Auto-scroll or skip when target is off-screen
    LaunchedEffect(currentTargetId) {
        if (currentTargetId == null || !config.waitForVisibility) return@LaunchedEffect

        // Small delay to let layout settle after step change
        delay(50)

        if (!controller.isTargetVisible(currentTargetId)) {
            val scroller = controller.scrollRequester
            if (scroller != null) {
                // Request scroll to bring target into view
                scroller(currentTargetId)
                // Wait for scroll + layout to settle, then check if it worked
                delay(config.scrollTimeout)
                if (!controller.isTargetVisible(currentTargetId)) {
                    controller.skipCurrentIfNotVisible()
                }
            } else {
                // No scroller provided — skip this step
                controller.skipCurrentIfNotVisible()
            }
        }
    }

    // Wait for visibility + scroll idle + delay before showing
    LaunchedEffect(currentTargetId, isScrolling) {
        if (currentTargetId == null) {
            isReadyToShow = false
            return@LaunchedEffect
        }

        if (config.waitForVisibility) {
            // Wait for scroll to stop
            if (isScrolling) {
                isReadyToShow = false
                return@LaunchedEffect
            }

            // Check if target is visible
            if (!controller.isTargetVisible(currentTargetId)) {
                isReadyToShow = false
                return@LaunchedEffect
            }

            // Apply delay after scroll stops
            delay(config.visibilityDelay)
        }

        isReadyToShow = true
    }

    // Don't render if waiting for visibility
    if (config.waitForVisibility && !isReadyToShow && state !is CoachmarkState.Hidden) {
        return
    }

    when (val currentState = state) {
        CoachmarkState.Hidden -> {
            // Nothing to render
        }
        is CoachmarkState.Showing -> {
            CoachmarkScrimContent(
                target = currentState.target,
                currentStep = currentState.currentStep,
                totalSteps = currentState.totalSteps,
                isFirstStep = true,
                config = config,
                colors = colors,
                onNext = {
                    currentOnStepCompleted(0, currentState.target.id)
                    controller.next()
                    currentOnDismiss()
                },
                onBack = {
                    controller.dismiss()
                    currentOnDismiss()
                },
                onDismiss = {
                    controller.dismiss()
                    currentOnDismiss()
                },
                modifier = modifier,
            )
        }
        is CoachmarkState.Sequence -> {
            CoachmarkScrimContent(
                target = currentState.currentTarget,
                currentStep = currentState.currentStep,
                totalSteps = currentState.totalSteps,
                isFirstStep = !currentState.hasPrevious,
                config = config,
                colors = colors,
                onNext = {
                    val stepIndex = currentState.currentIndex
                    val targetId = currentState.currentTarget.id
                    currentOnStepCompleted(stepIndex, targetId)
                    controller.next()
                    if (!currentState.hasNext) {
                        currentOnDismiss()
                    }
                },
                onBack = {
                    if (config.backPressBehavior == BackPressBehavior.NAVIGATE && currentState.hasPrevious) {
                        controller.previous()
                    } else {
                        controller.dismiss()
                        currentOnDismiss()
                    }
                },
                onDismiss = {
                    controller.dismiss()
                    currentOnDismiss()
                },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun CoachmarkScrimContent(
    target: CoachmarkTarget,
    currentStep: Int,
    totalSteps: Int,
    isFirstStep: Boolean,
    config: CoachmarkConfig,
    colors: CoachmarkColors,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    // Animation states
    val overlayAlpha = remember { Animatable(if (isFirstStep) 0f else 1f) }
    val connectorProgress = remember { Animatable(0f) }
    val tooltipAlpha = remember { Animatable(0f) }

    var screenSize by remember { mutableStateOf(IntSize.Zero) }
    var tooltipSize by remember { mutableStateOf(IntSize.Zero) }

    val connectorLengthPx = if (target.connectorLength.isSpecified) {
        with(density) { target.connectorLength.toPx() }
    } else {
        0f
    }

    val tooltipPosition =
        remember(target, screenSize, tooltipSize, connectorLengthPx) {
            calculateTooltipPosition(
                targetBounds = target.bounds,
                tooltipSize = tooltipSize,
                screenSize = screenSize,
                tooltipPosition = target.tooltipPosition,
                margin = with(density) { config.tooltipMargin.toPx() },
                gap = with(density) { config.tooltipGap.toPx() },
                connectorLength = connectorLengthPx,
            )
        }

    val connectorTooltipGapPx = with(density) { config.connectorTooltipGap.toPx() }
    val strokeWidthPx = with(density) { config.strokeWidth.toPx() }
    val connectorDotRadiusPx = with(density) { config.connectorDotRadius.toPx() }
    val connectorPoints =
        remember(target, tooltipPosition, tooltipSize, density, connectorTooltipGapPx, strokeWidthPx, connectorDotRadiusPx) {
            calculateConnectorPoints(
                target = target,
                tooltipPosition = tooltipPosition,
                tooltipSize = tooltipSize,
                density = density,
                connectorTooltipGap = connectorTooltipGapPx,
                strokeWidth = strokeWidthPx,
                connectorDotRadius = connectorDotRadiusPx,
            )
        }

    // Start animations
    LaunchedEffect(target.id) {
        if (isFirstStep) {
            overlayAlpha.snapTo(0f)
        }
        connectorProgress.snapTo(0f)
        tooltipAlpha.snapTo(0f)

        if (isFirstStep && config.delayBeforeShow > 0L) {
            delay(config.delayBeforeShow)
        }

        if (overlayAlpha.value < 1f) {
            overlayAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = config.fadeAnimationDuration,
                    easing = FastOutSlowInEasing,
                ),
            )
        }

        connectorProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = config.connectorAnimationDuration,
                easing = LinearEasing,
            ),
        )

        tooltipAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = config.tooltipAnimationDuration,
                easing = FastOutSlowInEasing,
            ),
        )
    }

    PlatformBackHandler(onBack = onBack)

    // Pulse/glow animation
    val highlightAnimation = target.highlightAnimation ?: config.highlightAnimation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = config.pulseDurationMs / 2,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )

    val glowStrokeMultiplier by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = config.pulseDurationMs / 2,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowStroke",
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = config.pulseDurationMs / 2,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )

    // Ripple animation - expanding ring progress (0 to 1)
    val rippleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = config.pulseDurationMs,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rippleProgress",
    )

    // Shimmer animation - rotation around stroke (0 to 360 degrees)
    val shimmerAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = config.pulseDurationMs,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerAngle",
    )

    // Bounce animation - scale with overshoot
    val bounceScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = config.pulseDurationMs
                1f at 0 using LinearEasing
                1.15f at (config.pulseDurationMs * 0.2f).toInt() using FastOutSlowInEasing
                0.95f at (config.pulseDurationMs * 0.4f).toInt() using FastOutSlowInEasing
                1.02f at (config.pulseDurationMs * 0.6f).toInt() using FastOutSlowInEasing
                1f at config.pulseDurationMs using FastOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "bounceScale",
    )

    val effectiveScale = when (highlightAnimation) {
        HighlightAnimation.PULSE -> pulseScale
        HighlightAnimation.BOUNCE -> bounceScale
        else -> 1f
    }
    val effectiveStrokeMultiplier = when (highlightAnimation) {
        HighlightAnimation.GLOW -> glowStrokeMultiplier
        else -> 1f
    }
    val effectiveStrokeAlpha = when (highlightAnimation) {
        HighlightAnimation.GLOW -> glowAlpha
        else -> 1f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                screenSize = coordinates.size
            }
            .graphicsLayer {
                alpha = overlayAlpha.value
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .pointerInput(config.scrimTapBehavior) {
                detectTapGestures { offset ->
                    if (!target.bounds.contains(offset)) {
                        when (config.scrimTapBehavior) {
                            ScrimTapBehavior.DISMISS -> onDismiss()
                            ScrimTapBehavior.ADVANCE -> onNext()
                            ScrimTapBehavior.NONE -> { /* Do nothing */ }
                        }
                    }
                }
            }
            .semantics {
                contentDescription = "Coachmark overlay: ${target.title}. ${target.description}. " +
                    "Tap Got it to continue or tap outside to dismiss."
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scrimColor = config.scrimOpacity?.let {
                Color.Black.copy(alpha = it.alpha)
            } ?: colors.scrimColor
            drawRect(color = scrimColor)

            drawCutout(target = target, density = density)

            // Draw glow rings for GLOW animation
            if (highlightAnimation == HighlightAnimation.GLOW) {
                val baseStrokeWidth = with(density) { config.strokeWidth.toPx() }
                // Outer glow layers (drawn first, behind main stroke)
                drawCutoutStroke(
                    target = target,
                    strokeColor = colors.strokeColor.copy(alpha = 0.1f * glowAlpha),
                    strokeWidth = baseStrokeWidth * 6f * glowStrokeMultiplier,
                    density = density,
                    scale = effectiveScale,
                )
                drawCutoutStroke(
                    target = target,
                    strokeColor = colors.strokeColor.copy(alpha = 0.2f * glowAlpha),
                    strokeWidth = baseStrokeWidth * 4f * glowStrokeMultiplier,
                    density = density,
                    scale = effectiveScale,
                )
                drawCutoutStroke(
                    target = target,
                    strokeColor = colors.strokeColor.copy(alpha = 0.3f * glowAlpha),
                    strokeWidth = baseStrokeWidth * 2.5f * glowStrokeMultiplier,
                    density = density,
                    scale = effectiveScale,
                )
            }

            // Draw ripple rings for RIPPLE animation
            if (highlightAnimation == HighlightAnimation.RIPPLE) {
                val baseStrokeWidth = with(density) { config.strokeWidth.toPx() }
                // Draw 3 staggered ripple rings
                for (i in 0 until 3) {
                    val staggeredProgress = (rippleProgress + i * 0.33f) % 1f
                    val rippleScale = 1f + staggeredProgress * 0.3f
                    val rippleAlpha = (1f - staggeredProgress).coerceIn(0f, 1f) * 0.7f
                    if (rippleAlpha > 0.01f) {
                        drawCutoutStroke(
                            target = target,
                            strokeColor = colors.strokeColor.copy(alpha = rippleAlpha),
                            strokeWidth = baseStrokeWidth * (1f + staggeredProgress),
                            density = density,
                            scale = rippleScale,
                        )
                    }
                }
            }

            // Draw shimmer effect for SHIMMER animation
            if (highlightAnimation == HighlightAnimation.SHIMMER) {
                drawShimmerEffect(
                    target = target,
                    shimmerAngle = shimmerAngle,
                    strokeColor = colors.strokeColor,
                    strokeWidth = with(density) { config.strokeWidth.toPx() },
                    density = density,
                )
            }

            drawCutoutStroke(
                target = target,
                strokeColor = colors.strokeColor.copy(alpha = effectiveStrokeAlpha),
                strokeWidth = with(density) { config.strokeWidth.toPx() } * effectiveStrokeMultiplier,
                density = density,
                scale = effectiveScale,
            )

            if (connectorProgress.value > 0f && connectorPoints.isNotEmpty()) {
                drawConnectorPath(
                    points = connectorPoints,
                    progress = connectorProgress.value,
                    color = colors.connectorColor,
                    strokeWidth = with(density) { config.strokeWidth.toPx() },
                    dotRadius = with(density) { config.connectorDotRadius.toPx() },
                )
            }
        }

        val tooltipEffectiveAlpha = if (tooltipSize == IntSize.Zero) 0f else tooltipAlpha.value

        TooltipContainer(
            target = target,
            currentStep = currentStep,
            totalSteps = totalSteps,
            position = tooltipPosition,
            alpha = tooltipEffectiveAlpha,
            colors = colors,
            config = config,
            onSizeChanged = { tooltipSize = it },
            onNext = onNext,
            onSkip = onDismiss,
        )
    }
}

@Composable
private fun BoxScope.TooltipContainer(
    target: CoachmarkTarget,
    currentStep: Int,
    totalSteps: Int,
    position: Offset,
    alpha: Float,
    colors: CoachmarkColors,
    config: CoachmarkConfig,
    onSizeChanged: (IntSize) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    val showProgressIndicator = target.showProgressIndicator ?: config.showProgressIndicator

    Box(
        modifier = Modifier
            .offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
            .graphicsLayer { this.alpha = alpha }
            .onGloballyPositioned { coordinates ->
                onSizeChanged(coordinates.size)
            }
            .padding(config.tooltipMargin),
    ) {
        CoachmarkTooltip(
            title = target.title,
            description = target.description,
            ctaText = target.ctaText,
            currentStep = currentStep,
            totalSteps = totalSteps,
            colors = colors,
            cornerRadius = config.tooltipCornerRadius,
            showProgressIndicator = showProgressIndicator,
            showCard = config.showTooltipCard,
            showSkipButton = config.showSkipButton,
            skipButtonText = config.skipButtonText,
            onCtaClick = onNext,
            onSkipClick = onSkip,
        )
    }
}

private fun DrawScope.drawCutout(
    target: CoachmarkTarget,
    density: androidx.compose.ui.unit.Density,
) {
    when (val shape = target.shape) {
        is CutoutShape.Circle -> {
            val padding = with(density) { shape.radiusPadding.toPx() }
            val radius = if (shape.radius.isSpecified) {
                with(density) { shape.radius.toPx() } + padding
            } else {
                maxOf(target.bounds.width, target.bounds.height) / 2 + padding
            }
            drawCircle(
                color = Color.Black,
                radius = radius,
                center = target.bounds.center,
                blendMode = BlendMode.Clear,
            )
        }
        is CutoutShape.RoundedRect -> {
            val padding = with(density) { shape.padding.toPx() }
            val cornerRadius = with(density) { shape.cornerRadius.toPx() }
            val paddedBounds = Rect(
                left = target.bounds.left - padding,
                top = target.bounds.top - padding,
                right = target.bounds.right + padding,
                bottom = target.bounds.bottom + padding,
            )
            drawRoundRect(
                color = Color.Black,
                topLeft = paddedBounds.topLeft,
                size = Size(paddedBounds.width, paddedBounds.height),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                blendMode = BlendMode.Clear,
            )
        }
        is CutoutShape.Rect -> {
            val padding = with(density) { shape.padding.toPx() }
            val paddedBounds = Rect(
                left = target.bounds.left - padding,
                top = target.bounds.top - padding,
                right = target.bounds.right + padding,
                bottom = target.bounds.bottom + padding,
            )
            drawRect(
                color = Color.Black,
                topLeft = paddedBounds.topLeft,
                size = Size(paddedBounds.width, paddedBounds.height),
                blendMode = BlendMode.Clear,
            )
        }
        is CutoutShape.Squircle -> {
            val padding = with(density) { shape.padding.toPx() }
            val cornerRadius = with(density) { shape.cornerRadius.toPx() }
            val path = createPaddedSquirclePath(
                bounds = target.bounds,
                padding = padding,
                cornerRadius = cornerRadius,
            )
            drawPath(
                path = path,
                color = Color.Black,
                blendMode = BlendMode.Clear,
            )
        }
        is CutoutShape.Star -> {
            val padding = with(density) { shape.padding.toPx() }
            val path = createPaddedStarPath(
                bounds = target.bounds,
                padding = padding,
                points = shape.points,
                innerRadiusRatio = shape.innerRadiusRatio,
            )
            drawPath(
                path = path,
                color = Color.Black,
                blendMode = BlendMode.Clear,
            )
        }
    }
}

private fun DrawScope.drawCutoutStroke(
    target: CoachmarkTarget,
    strokeColor: Color,
    strokeWidth: Float,
    density: androidx.compose.ui.unit.Density,
    scale: Float = 1f,
) {
    when (val shape = target.shape) {
        is CutoutShape.Circle -> {
            val padding = with(density) { shape.radiusPadding.toPx() }
            val baseRadius = if (shape.radius.isSpecified) {
                with(density) { shape.radius.toPx() } + padding
            } else {
                maxOf(target.bounds.width, target.bounds.height) / 2 + padding
            }
            val radius = baseRadius * scale
            drawCircle(
                color = strokeColor,
                radius = radius,
                center = target.bounds.center,
                style = Stroke(width = strokeWidth),
            )
        }
        is CutoutShape.RoundedRect -> {
            val padding = with(density) { shape.padding.toPx() }
            val cornerRadius = with(density) { shape.cornerRadius.toPx() }
            val scaledPadding = padding + (target.bounds.width / 2) * (scale - 1f)
            val paddedBounds = Rect(
                left = target.bounds.left - scaledPadding,
                top = target.bounds.top - scaledPadding,
                right = target.bounds.right + scaledPadding,
                bottom = target.bounds.bottom + scaledPadding,
            )
            drawRoundRect(
                color = strokeColor,
                topLeft = paddedBounds.topLeft,
                size = Size(paddedBounds.width, paddedBounds.height),
                cornerRadius = CornerRadius(cornerRadius * scale, cornerRadius * scale),
                style = Stroke(width = strokeWidth),
            )
        }
        is CutoutShape.Rect -> {
            val padding = with(density) { shape.padding.toPx() }
            val scaledPadding = padding + (target.bounds.width / 2) * (scale - 1f)
            val paddedBounds = Rect(
                left = target.bounds.left - scaledPadding,
                top = target.bounds.top - scaledPadding,
                right = target.bounds.right + scaledPadding,
                bottom = target.bounds.bottom + scaledPadding,
            )
            drawRect(
                color = strokeColor,
                topLeft = paddedBounds.topLeft,
                size = Size(paddedBounds.width, paddedBounds.height),
                style = Stroke(width = strokeWidth),
            )
        }
        is CutoutShape.Squircle -> {
            val padding = with(density) { shape.padding.toPx() }
            val cornerRadius = with(density) { shape.cornerRadius.toPx() }
            val scaledPadding = padding + (target.bounds.width / 2) * (scale - 1f)
            val path = createPaddedSquirclePath(
                bounds = target.bounds,
                padding = scaledPadding,
                cornerRadius = cornerRadius * scale,
            )
            drawPath(
                path = path,
                color = strokeColor,
                style = Stroke(width = strokeWidth),
            )
        }
        is CutoutShape.Star -> {
            val padding = with(density) { shape.padding.toPx() }
            val path = createScaledStarPath(
                bounds = target.bounds,
                padding = padding,
                points = shape.points,
                innerRadiusRatio = shape.innerRadiusRatio,
                scale = scale,
            )
            drawPath(
                path = path,
                color = strokeColor,
                style = Stroke(width = strokeWidth),
            )
        }
    }
}

private fun DrawScope.drawConnectorPath(
    points: List<Offset>,
    progress: Float,
    color: Color,
    strokeWidth: Float,
    dotRadius: Float,
) {
    if (points.size < 2) return

    var totalLength = 0f
    val segmentLengths = mutableListOf<Float>()
    for (i in 0 until points.size - 1) {
        val dx = points[i + 1].x - points[i].x
        val dy = points[i + 1].y - points[i].y
        val segmentLength = kotlin.math.sqrt(dx * dx + dy * dy)
        segmentLengths.add(segmentLength)
        totalLength += segmentLength
    }

    val drawLength = totalLength * progress
    var drawnLength = 0f

    for (i in 0 until points.size - 1) {
        val segmentLength = segmentLengths[i]
        val remainingToDraw = drawLength - drawnLength

        if (remainingToDraw <= 0) break

        val segmentProgress = (remainingToDraw / segmentLength).coerceAtMost(1f)
        val segmentEnd = Offset(
            x = points[i].x + (points[i + 1].x - points[i].x) * segmentProgress,
            y = points[i].y + (points[i + 1].y - points[i].y) * segmentProgress,
        )

        drawLine(
            color = color,
            start = points[i],
            end = segmentEnd,
            strokeWidth = strokeWidth,
        )

        drawnLength += segmentLength
    }

    if (progress >= 1f) {
        drawCircle(
            color = color,
            radius = dotRadius,
            center = points.last(),
        )
    }
}

private fun calculateTooltipPosition(
    targetBounds: Rect,
    tooltipSize: IntSize,
    screenSize: IntSize,
    tooltipPosition: TooltipPosition,
    margin: Float,
    gap: Float,
    connectorLength: Float,
): Offset {
    if (screenSize == IntSize.Zero) {
        return Offset.Zero
    }

    val screenCenterX = screenSize.width / 2f
    val targetCenterY = targetBounds.center.y

    val preferBottom = tooltipPosition == TooltipPosition.BOTTOM ||
        (tooltipPosition == TooltipPosition.AUTO && targetCenterY < screenSize.height / 2)

    // Use connectorLength if specified, otherwise use the default gap
    val effectiveGap = if (connectorLength > 0f) connectorLength else gap

    val y = if (preferBottom) {
        targetBounds.bottom + effectiveGap
    } else {
        targetBounds.top - effectiveGap - tooltipSize.height
    }.coerceIn(margin, screenSize.height - tooltipSize.height - margin)

    val x = when (tooltipPosition) {
        TooltipPosition.START -> margin
        TooltipPosition.END -> screenSize.width - tooltipSize.width - margin
        else -> {
            val targetCenterX = targetBounds.center.x
            if (targetCenterX < screenCenterX) {
                margin
            } else {
                (screenSize.width - tooltipSize.width - margin).coerceAtLeast(margin)
            }
        }
    }

    return Offset(x, y)
}

private fun calculateConnectorPoints(
    target: CoachmarkTarget,
    tooltipPosition: Offset,
    tooltipSize: IntSize,
    density: androidx.compose.ui.unit.Density,
    connectorTooltipGap: Float,
    strokeWidth: Float = 0f,
    connectorDotRadius: Float = 0f,
): List<Offset> {
    if (tooltipSize == IntSize.Zero) {
        return emptyList()
    }

    val targetBounds = target.bounds
    val targetCenter = targetBounds.center
    val shape = target.shape
    val connectorStyle = target.connectorStyle

    val defaultLineLength = with(density) { 40.dp.toPx() }
    val lineLength = if (target.connectorLength.isSpecified) {
        with(density) { target.connectorLength.toPx() }
    } else {
        defaultLineLength
    }

    // Gap between cutout stroke outer edge and connector start.
    // Stroke is centered on cutoutRadius, so outer edge = cutoutRadius + strokeWidth/2.
    // We offset by strokeWidth/2 (to reach outer edge) + dotRadius + strokeWidth (breathing room)
    // so the connector line and its dot fully clear the cutout stroke.
    val cutoutStrokeGap = strokeWidth / 2f + connectorDotRadius + strokeWidth

    val cutoutRadius = when (shape) {
        is CutoutShape.Circle -> {
            val padding = with(density) { shape.radiusPadding.toPx() }
            if (shape.radius.isSpecified) {
                with(density) { shape.radius.toPx() } + padding
            } else {
                maxOf(targetBounds.width, targetBounds.height) / 2 + padding
            }
        }
        is CutoutShape.RoundedRect -> {
            val padding = with(density) { shape.padding.toPx() }
            maxOf(targetBounds.width, targetBounds.height) / 2 + padding
        }
        is CutoutShape.Rect -> {
            val padding = with(density) { shape.padding.toPx() }
            maxOf(targetBounds.width, targetBounds.height) / 2 + padding
        }
        is CutoutShape.Squircle -> {
            val padding = with(density) { shape.padding.toPx() }
            maxOf(targetBounds.width, targetBounds.height) / 2 + padding
        }
        is CutoutShape.Star -> {
            val padding = with(density) { shape.padding.toPx() }
            maxOf(targetBounds.width, targetBounds.height) / 2 + padding
        }
    }

    val tooltipCenterX = tooltipPosition.x + tooltipSize.width / 2f
    val tooltipCenterY = tooltipPosition.y + tooltipSize.height / 2f
    val isTooltipBelow = tooltipPosition.y > targetBounds.bottom

    val resolvedStyle = if (connectorStyle == ConnectorStyle.AUTO) {
        val horizontalDistance = kotlin.math.abs(tooltipCenterX - targetCenter.x)
        val verticalDistance = kotlin.math.abs(tooltipCenterY - targetCenter.y)

        val hasSignificantHorizontal = horizontalDistance > cutoutRadius * 2
        val hasSignificantVertical = verticalDistance > cutoutRadius * 2

        when {
            hasSignificantHorizontal && hasSignificantVertical -> ConnectorStyle.ELBOW
            verticalDistance > horizontalDistance -> ConnectorStyle.VERTICAL
            else -> ConnectorStyle.HORIZONTAL
        }
    } else {
        connectorStyle
    }

    return when (resolvedStyle) {
        ConnectorStyle.AUTO -> emptyList()

        ConnectorStyle.HORIZONTAL -> {
            val goingLeft = tooltipCenterX < targetCenter.x
            val startRadius = cutoutRadius + cutoutStrokeGap
            val cutoutEdgePoint = Offset(
                x = if (goingLeft) targetCenter.x - startRadius else targetCenter.x + startRadius,
                y = targetCenter.y,
            )
            val endPointX = if (goingLeft) {
                cutoutEdgePoint.x - lineLength
            } else {
                cutoutEdgePoint.x + lineLength
            }
            val endPoint = Offset(x = endPointX, y = targetCenter.y)
            listOf(cutoutEdgePoint, endPoint)
        }

        ConnectorStyle.VERTICAL -> {
            val direction = if (isTooltipBelow) 1f else -1f
            val startRadius = cutoutRadius + cutoutStrokeGap
            val cutoutEdgePoint = Offset(
                x = targetCenter.x,
                y = targetCenter.y + direction * startRadius,
            )
            val endPointY = if (isTooltipBelow) {
                tooltipPosition.y - connectorTooltipGap
            } else {
                tooltipPosition.y + tooltipSize.height + connectorTooltipGap
            }
            val endPoint = Offset(x = targetCenter.x, y = endPointY)
            listOf(cutoutEdgePoint, endPoint)
        }

        ConnectorStyle.ELBOW -> {
            val goingLeft = tooltipCenterX < targetCenter.x
            val startRadius = cutoutRadius + cutoutStrokeGap
            val cutoutEdgePoint = Offset(
                x = if (goingLeft) targetCenter.x - startRadius else targetCenter.x + startRadius,
                y = targetCenter.y,
            )
            val cornerX = if (goingLeft) {
                cutoutEdgePoint.x - lineLength
            } else {
                cutoutEdgePoint.x + lineLength
            }
            val cornerPoint = Offset(x = cornerX, y = targetCenter.y)
            val endPointY = if (isTooltipBelow) {
                tooltipPosition.y - connectorTooltipGap
            } else {
                tooltipPosition.y + tooltipSize.height + connectorTooltipGap
            }
            val endPoint = Offset(x = cornerX, y = endPointY)
            listOf(cutoutEdgePoint, cornerPoint, endPoint)
        }

        ConnectorStyle.DIRECT -> {
            val tooltipConnectionX = tooltipCenterX
            val tooltipConnectionPoint = if (isTooltipBelow) {
                Offset(
                    x = tooltipConnectionX.coerceIn(
                        tooltipPosition.x + 20f,
                        tooltipPosition.x + tooltipSize.width - 20f,
                    ),
                    y = tooltipPosition.y,
                )
            } else {
                Offset(
                    x = tooltipConnectionX.coerceIn(
                        tooltipPosition.x + 20f,
                        tooltipPosition.x + tooltipSize.width - 20f,
                    ),
                    y = tooltipPosition.y + tooltipSize.height,
                )
            }

            val dx = tooltipConnectionPoint.x - targetCenter.x
            val dy = tooltipConnectionPoint.y - targetCenter.y
            val distance = kotlin.math.sqrt(dx * dx + dy * dy)

            if (distance < 1f) {
                return emptyList()
            }

            val normalizedDx = dx / distance
            val normalizedDy = dy / distance

            val startRadius = cutoutRadius + cutoutStrokeGap
            val cutoutEdgePoint = Offset(
                x = targetCenter.x + normalizedDx * startRadius,
                y = targetCenter.y + normalizedDy * startRadius,
            )
            listOf(cutoutEdgePoint, tooltipConnectionPoint)
        }
    }
}

/**
 * Draws a shimmer effect around the cutout stroke.
 * Creates a moving highlight that travels around the perimeter.
 */
private fun DrawScope.drawShimmerEffect(
    target: CoachmarkTarget,
    shimmerAngle: Float,
    strokeColor: Color,
    strokeWidth: Float,
    density: androidx.compose.ui.unit.Density,
) {
    val center = target.bounds.center
    val angleRad = shimmerAngle.toDouble() * PI / 180.0

    // Calculate shimmer highlight position based on angle
    val shimmerRadius = when (val shape = target.shape) {
        is CutoutShape.Circle -> {
            val padding = with(density) { shape.radiusPadding.toPx() }
            if (shape.radius.isSpecified) {
                with(density) { shape.radius.toPx() } + padding
            } else {
                maxOf(target.bounds.width, target.bounds.height) / 2 + padding
            }
        }
        is CutoutShape.RoundedRect -> {
            val padding = with(density) { shape.padding.toPx() }
            maxOf(target.bounds.width, target.bounds.height) / 2 + padding
        }
        is CutoutShape.Rect -> {
            val padding = with(density) { shape.padding.toPx() }
            maxOf(target.bounds.width, target.bounds.height) / 2 + padding
        }
        is CutoutShape.Squircle -> {
            val padding = with(density) { shape.padding.toPx() }
            maxOf(target.bounds.width, target.bounds.height) / 2 + padding
        }
        is CutoutShape.Star -> {
            val padding = with(density) { shape.padding.toPx() }
            maxOf(target.bounds.width, target.bounds.height) / 2 + padding
        }
    }

    // Draw multiple shimmer dots at different positions around the stroke
    val shimmerSpread = 30f // degrees of spread for the shimmer trail
    for (i in 0 until 5) {
        val dotAngle = angleRad - (i * shimmerSpread / 5).toDouble() * PI / 180.0
        val alpha = (1f - i * 0.2f).coerceIn(0f, 1f)
        val dotRadius = (strokeWidth * 2f) * (1f - i * 0.15f)

        val dotX = center.x + (shimmerRadius * cos(dotAngle)).toFloat()
        val dotY = center.y + (shimmerRadius * sin(dotAngle)).toFloat()

        drawCircle(
            color = Color.White.copy(alpha = alpha * 0.8f),
            radius = dotRadius,
            center = Offset(dotX, dotY),
        )
    }

    // Draw a brighter leading dot
    val leadX = center.x + (shimmerRadius * cos(angleRad)).toFloat()
    val leadY = center.y + (shimmerRadius * sin(angleRad)).toFloat()
    drawCircle(
        color = Color.White,
        radius = strokeWidth * 2.5f,
        center = Offset(leadX, leadY),
    )
}
