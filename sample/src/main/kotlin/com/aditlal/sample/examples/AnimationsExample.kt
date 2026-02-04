package com.aditlal.sample.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkConfig
import io.luminos.CoachmarkHost
import io.luminos.CoachmarkTarget
import io.luminos.ConnectorStyle
import io.luminos.CutoutShape
import io.luminos.HighlightAnimation
import io.luminos.coachmarkColors
import io.luminos.coachmarkTarget
import io.luminos.rememberCoachmarkController

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AnimationsExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val controller = rememberCoachmarkController()
    var selectedAnimation by remember { mutableStateOf<HighlightAnimation?>(null) }
    var showCoachmark by remember { mutableStateOf(false) }

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            highlightAnimation = selectedAnimation ?: HighlightAnimation.NONE,
            pulseDurationMs = 1200,
        ),
        colors = coachmarkColors(),
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Highlight Animations") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Highlight Animations",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "6 animation styles to draw attention to targets",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Single target
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .coachmarkTarget(controller, "target"),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Target",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(36.dp),
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Select an animation style:",
                    style = MaterialTheme.typography.labelLarge,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Animation buttons in a flow layout
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AnimationButton("None", selectedAnimation == HighlightAnimation.NONE) {
                        selectedAnimation = HighlightAnimation.NONE
                        showCoachmark = true
                    }
                    AnimationButton("Pulse", selectedAnimation == HighlightAnimation.PULSE) {
                        selectedAnimation = HighlightAnimation.PULSE
                        showCoachmark = true
                    }
                    AnimationButton("Glow", selectedAnimation == HighlightAnimation.GLOW) {
                        selectedAnimation = HighlightAnimation.GLOW
                        showCoachmark = true
                    }
                    AnimationButton("Ripple", selectedAnimation == HighlightAnimation.RIPPLE) {
                        selectedAnimation = HighlightAnimation.RIPPLE
                        showCoachmark = true
                    }
                    AnimationButton("Shimmer", selectedAnimation == HighlightAnimation.SHIMMER) {
                        selectedAnimation = HighlightAnimation.SHIMMER
                        showCoachmark = true
                    }
                    AnimationButton("Bounce", selectedAnimation == HighlightAnimation.BOUNCE) {
                        selectedAnimation = HighlightAnimation.BOUNCE
                        showCoachmark = true
                    }
                }
            }
        }
    }

    if (showCoachmark) {
        val (title, description) = when (selectedAnimation) {
            HighlightAnimation.NONE -> "No Animation" to "Static cutout with no animation. Clean and minimal."
            HighlightAnimation.PULSE -> "Pulse Animation" to "Gentle breathing effect that scales the stroke outward."
            HighlightAnimation.GLOW -> "Glow Animation" to "Radiating glow rings that pulse. Great for high-priority targets."
            HighlightAnimation.RIPPLE -> "Ripple Animation" to "Expanding rings that fade out like ripples in water."
            HighlightAnimation.SHIMMER -> "Shimmer Animation" to "A bright highlight that orbits around the target."
            HighlightAnimation.BOUNCE -> "Bounce Animation" to "Energetic pop effect with overshoot. Playful and attention-grabbing."
            null -> "" to ""
        }
        controller.show(
            CoachmarkTarget(
                id = "target",
                title = title,
                description = description,
                shape = CutoutShape.Circle(radiusPadding = 16.dp),
                connectorStyle = ConnectorStyle.VERTICAL,
                connectorLength = 80.dp,
            )
        )
        showCoachmark = false
    }
}

@Composable
private fun AnimationButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    if (isSelected) {
        androidx.compose.material3.Button(onClick = onClick) {
            Text(label)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(label)
        }
    }
}
