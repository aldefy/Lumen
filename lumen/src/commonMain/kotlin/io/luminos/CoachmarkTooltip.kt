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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
 * @param showCard Whether to wrap content in a card/box background
 * @param showSkipButton Whether to show a skip button to dismiss the entire sequence
 * @param skipButtonText Text for the skip button
 * @param showDontShowAgain Whether to show a "Don't show again" checkbox
 * @param dontShowAgainText Text label for the checkbox
 * @param dontShowAgainChecked Current checked state of the checkbox
 * @param onDontShowAgainChanged Callback when the checkbox state changes
 * @param onCtaClick Callback when CTA is clicked
 * @param onSkipClick Callback when Skip is clicked (dismisses entire coachmark)
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
) {
    val cardModifier =
        if (showCard) {
            Modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(colors.tooltipCardColor)
                .padding(16.dp)
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
                        style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
                    )
                }
            }
        }

        // Title
        Text(
            text = title,
            color = titleTextColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 26.sp,
            style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
            modifier = Modifier.semantics { heading() },
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Description
        Text(
            text = description,
            color = descriptionTextColor,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Footer with progress indicator and CTA
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Progress indicator (only show for sequences with multiple steps, if enabled)
            if (totalSteps > 1 && showProgressIndicator) {
                ProgressIndicator(
                    currentStep = currentStep,
                    totalSteps = totalSteps,
                    activeColor = colors.progressActiveColor,
                    inactiveColor = colors.progressInactiveColor,
                )
            } else {
                Spacer(modifier = Modifier.width(1.dp))
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
            val isActive = index < currentStep
            Box(
                modifier =
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isActive) activeColor else inactiveColor),
            )
        }
    }
}
