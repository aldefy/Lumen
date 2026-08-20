package io.luminos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp

/**
 * Tooltip for displaying coachmark content.
 *
 * @param title The headline text
 * @param description The body text
 * @param ctaText Text for the call-to-action button
 * @param currentStep Current step number (1-indexed)
 * @param totalSteps Total number of steps
 * @param colors Theme colors
 * @param cornerRadius Corner radius for the card
 * @param showProgressIndicator Whether to show progress dots for multi-step sequences
 * @param progressIndicator Optional slot replacing the built-in progress dots entirely.
 *   Receives the 1-based current step and the total step count. When `null` (default) the
 *   built-in dots (or pill, per [progressIndicatorStyle]) render, so existing callers are
 *   unaffected. Use this for a numeric "2 / 5" label, a segmented bar, or anything else — no
 *   library change required. Takes priority over [progressIndicatorStyle] when set. Only
 *   invoked when [showProgressIndicator] is true and [totalSteps] is greater than 1.
 * @param progressIndicatorStyle Visual style for the built-in progress indicator (dots, or a
 *   pill for the current step). Ignored when [progressIndicator] is set.
 * @param progressActivePillWidth Width of the active pill when [progressIndicatorStyle] is [ProgressIndicatorStyle.PILL]
 * @param showCard Whether to wrap content in a card/box background
 * @param showSkipButton Whether to show a skip button to dismiss the entire sequence
 * @param skipButtonText Text for the skip button
 * @param showDontShowAgain Whether to show a "Don't show again" checkbox
 * @param dontShowAgainText Text label for the checkbox
 * @param dontShowAgainChecked Current checked state of the checkbox
 * @param onDontShowAgainChanged Callback when the checkbox state changes
 * @param onCtaClick Callback when CTA is clicked
 * @param onSkipClick Callback when Skip is clicked (dismisses entire coachmark)
 * @param titleInlineWithConnector When true, the title is rendered inline with the connector dot.
 *   This forces [TextAlign.Start] on the title regardless of [titleTextAlign] to ensure proper alignment
 *   with the dot.
 */
@Composable
fun CoachmarkTooltip(
    title: String,
    description: String,
    ctaText: String,
    currentStep: Int,
    totalSteps: Int,
    colors: CoachmarkColors,
    cornerRadius: Dp = 16.dp,
    showProgressIndicator: Boolean = true,
    progressIndicatorStyle: ProgressIndicatorStyle = ProgressIndicatorStyle.DOTS,
    progressActivePillWidth: Dp = 20.dp,
    showCard: Boolean = false,
    showSkipButton: Boolean = false,
    skipButtonText: String = "Skip",
    showDontShowAgain: Boolean = false,
    dontShowAgainText: String = "Don't show again",
    dontShowAgainChecked: Boolean = false,
    onDontShowAgainChanged: (Boolean) -> Unit = {},
    ctaMinWidth: Dp = Dp.Unspecified,
    ctaMinHeight: Dp = 48.dp,
    ctaCornerRadius: Dp = 22.dp,
    onCtaClick: () -> Unit,
    onSkipClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    progressIndicator: (@Composable (currentStep: Int, totalSteps: Int) -> Unit)? = null,
    titleTextAlign: TextAlign = TextAlign.Start,
    descriptionTextAlign: TextAlign = TextAlign.Start,
    skipButtonTextAlign: TextAlign = TextAlign.End,
    dontShowAgainTextAlign: TextAlign = TextAlign.Start,
    ctaHorizontalArrangement: Arrangement.Horizontal = Arrangement.End,
    titleInlineWithConnector: Boolean = false,
    connectorDotColor: Color = Color.White,
    connectorDotRadius: Dp = 4.dp,
    connectorDotOffsetX: Dp = 0.dp,
    onDotPositioned: (Offset) -> Unit = {},
    isTooltipBelow: Boolean = true,
    tooltipShape: ((tailAnchorX: Dp, isTooltipBelow: Boolean) -> Shape)? = null,
    tailAnchorX: Dp = Dp.Unspecified,
    tooltipTailInset: Dp = 0.dp,
) {
    val hasTail = tooltipShape != null && tailAnchorX.isSpecified
    val cardModifier =
        if (showCard) {
            if (hasTail) {
                // The tail is carved into the card's own outline, so the Column's measured
                // size needs to be tooltipTailInset taller than the plain-card case — clip()
                // always bounds to its content's actual layout size, so there'd otherwise be
                // no room for the shape's tail to occupy. A Spacer inside the Column (below)
                // grows the content by exactly that amount on the tail's side; the shape then
                // reads that extra strip as free to draw the tail into (see SpeechBubbleShape).
                val resolvedTailShape = remember(tooltipShape, tailAnchorX, isTooltipBelow) {
                    object : Shape {
                        override fun createOutline(
                            size: Size,
                            layoutDirection: LayoutDirection,
                            density: Density,
                        ) = tooltipShape!!(tailAnchorX, isTooltipBelow)
                            .createOutline(size, layoutDirection, density)
                    }
                }
                Modifier
                    .clip(resolvedTailShape)
                    .background(colors.tooltipCardColor)
                    .padding(16.dp)
            } else {
                Modifier
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(colors.tooltipCardColor)
                    .padding(16.dp)
            }
        } else {
            Modifier
        }

    Column(
        modifier =
            modifier
                .widthIn(max = 280.dp)
                .then(cardModifier)
                .semantics {
                    contentDescription = "$title. $description. Step $currentStep of $totalSteps."
                },
    ) {
        // Grows the Column by tooltipTailInset on the tail's side, so the card shape (clipped
        // around this Column) has that much extra room to carve the tail into. See cardModifier.
        // isTooltipBelow=true means the tooltip sits below the target, so the tail points UP
        // (on the top edge) — space is reserved here, at the top.
        if (hasTail && isTooltipBelow) {
            Spacer(modifier = Modifier.height(tooltipTailInset))
        }

        // Text shadow for better readability (only needed when floating, not in card)
        val textShadow =
            if (showCard) {
                null
            } else {
                Shadow(
                    color = Color.Black.copy(alpha = 0.5f),
                    offset = Offset(1f, 1f),
                    blurRadius = 2f,
                )
            }

        // Use appropriate text colors based on card mode
        val titleTextColor = if (showCard) colors.titleColor else colors.strokeColor
        val descriptionTextColor = if (showCard) colors.descriptionColor else colors.strokeColor.copy(alpha = 0.9f)

        // Skip button (top-right)
        if (showSkipButton) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = onSkipClick,
                    modifier = Modifier.semantics {
                        role = Role.Button
                        contentDescription = "$skipButtonText. Dismisses all coachmarks."
                    },
                ) {
                    Text(
                        text = skipButtonText,
                        color = if (showCard) colors.descriptionColor else colors.strokeColor.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = skipButtonTextAlign,
                        style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
                    )
                }
            }
        }

        // Title
        if (titleInlineWithConnector && isTooltipBelow) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (connectorDotOffsetX > 0.dp) {
                    Spacer(modifier = Modifier.width(connectorDotOffsetX))
                }
                Box(
                    modifier = Modifier
                        .size(connectorDotRadius * 2)
                        .clip(CircleShape)
                        .background(connectorDotColor)
                        .onGloballyPositioned { coords ->
                            val pos = coords.positionInRoot()
                            val center = Offset(
                                pos.x + coords.size.width / 2f,
                                pos.y + coords.size.height / 2f,
                            )
                            onDotPositioned(center)
                        },
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    color = titleTextColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Start,
                    style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
                    modifier = Modifier.weight(1f).semantics { heading() },
                )
            }
        } else if (titleInlineWithConnector && !isTooltipBelow) {
            // Tooltip above target: indent title to align with the bottom dot's X position
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (connectorDotOffsetX > 0.dp) {
                    Spacer(modifier = Modifier.width(connectorDotOffsetX))
                }
                Text(
                    text = title,
                    color = titleTextColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Start,
                    style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
                    modifier = Modifier.weight(1f).semantics { heading() },
                )
            }
        } else {
            Text(
                text = title,
                color = titleTextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp,
                textAlign = titleTextAlign,
                style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
                modifier = Modifier.fillMaxWidth().semantics { heading() },
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // When title is inline with connector, indent description to align with title text
        val descriptionStartPadding = if (titleInlineWithConnector && isTooltipBelow) {
            // Tooltip below: title row has [offset + dot + 4dp spacer + text]
            connectorDotOffsetX + connectorDotRadius * 2 + 4.dp
        } else if (titleInlineWithConnector && !isTooltipBelow) {
            // Tooltip above: title row has [offset + text] (dot is at bottom)
            connectorDotOffsetX
        } else {
            0.dp
        }

        // Description
        Text(
            text = description,
            color = descriptionTextColor,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = descriptionTextAlign,
            style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
            modifier = Modifier.fillMaxWidth().padding(start = descriptionStartPadding),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Footer with progress indicator and CTA
        val hasProgressIndicator = totalSteps > 1 && showProgressIndicator
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = descriptionStartPadding),
            horizontalArrangement = if (hasProgressIndicator) Arrangement.SpaceBetween else ctaHorizontalArrangement,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Progress indicator (only show for sequences with multiple steps, if enabled)
            if (hasProgressIndicator) {
                if (progressIndicator != null) {
                    progressIndicator(currentStep, totalSteps)
                } else {
                    ProgressIndicator(
                        currentStep = currentStep,
                        totalSteps = totalSteps,
                        activeColor = colors.progressActiveColor,
                        inactiveColor = colors.progressInactiveColor,
                        style = progressIndicatorStyle,
                        activePillWidth = progressActivePillWidth,
                    )
                }
            }

            // CTA Button
            Button(
                onClick = onCtaClick,
                modifier =
                    Modifier
                        .heightIn(min = ctaMinHeight)
                        .then(
                            if (ctaMinWidth.isSpecified) Modifier.widthIn(min = ctaMinWidth)
                            else Modifier
                        )
                        .semantics {
                            role = Role.Button
                            contentDescription = "$ctaText. Advances to next step."
                        },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = colors.ctaButtonColor,
                        contentColor = colors.ctaTextColor,
                    ),
                shape = RoundedCornerShape(ctaCornerRadius),
            ) {
                Text(
                    text = ctaText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        // "Don't show again" checkbox
        if (showDontShowAgain) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = dontShowAgainChecked,
                        onValueChange = onDontShowAgainChanged,
                        role = Role.Checkbox,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = dontShowAgainChecked,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(
                        checkedColor = colors.ctaButtonColor,
                        uncheckedColor = if (showCard) colors.descriptionColor else colors.strokeColor.copy(alpha = 0.7f),
                        checkmarkColor = colors.ctaTextColor,
                    ),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = dontShowAgainText,
                    color = if (showCard) colors.descriptionColor else colors.strokeColor.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    textAlign = dontShowAgainTextAlign,
                    style = if (!showCard) {
                        TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.5f),
                                offset = Offset(1f, 1f),
                                blurRadius = 2f,
                            ),
                        )
                    } else {
                        TextStyle.Default
                    },
                )
            }
        }

        // Bottom dot for tooltip-above-target inline connector alignment
        if (titleInlineWithConnector && !isTooltipBelow) {
            Row(modifier = Modifier.fillMaxWidth()) {
                if (connectorDotOffsetX > 0.dp) {
                    Spacer(modifier = Modifier.width(connectorDotOffsetX))
                }
                Box(
                    modifier = Modifier
                        .size(connectorDotRadius * 2)
                        .clip(CircleShape)
                        .background(connectorDotColor)
                        .onGloballyPositioned { coords ->
                            val pos = coords.positionInRoot()
                            val center = Offset(
                                pos.x + coords.size.width / 2f,
                                pos.y + coords.size.height / 2f,
                            )
                            onDotPositioned(center)
                        },
                )
            }
        }

        if (hasTail && !isTooltipBelow) {
            Spacer(modifier = Modifier.height(tooltipTailInset))
        }
    }
}

/**
 * Dot-based progress indicator for coachmark sequences.
 */
@Composable
private fun ProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    activeColor: Color,
    inactiveColor: Color,
    style: ProgressIndicatorStyle = ProgressIndicatorStyle.DOTS,
    activePillWidth: Dp = 20.dp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .semantics {
                    contentDescription = "Step $currentStep of $totalSteps"
                },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSteps) { index ->
            when (style) {
                ProgressIndicatorStyle.DOTS -> {
                    val isActive = index < currentStep
                    Box(
                        modifier =
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isActive) activeColor else inactiveColor),
                    )
                }

                ProgressIndicatorStyle.PILL -> {
                    val isCurrent = index == currentStep - 1
                    Box(
                        modifier =
                            Modifier
                                .size(width = if (isCurrent) activePillWidth else 8.dp, height = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isCurrent) activeColor else inactiveColor),
                    )
                }
            }
        }
    }
}
