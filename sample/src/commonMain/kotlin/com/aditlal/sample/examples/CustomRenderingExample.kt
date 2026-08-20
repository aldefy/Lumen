package com.aditlal.sample.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkConfig
import io.luminos.CoachmarkController
import io.luminos.CoachmarkHost
import io.luminos.CoachmarkTarget
import io.luminos.ConnectorStyle
import io.luminos.CutoutShape
import io.luminos.ScrimTapBehavior
import io.luminos.coachmarkColors
import io.luminos.coachmarkTarget
import io.luminos.rememberCoachmarkController
import io.luminos.shapes.SpeechBubbleShape

/**
 * Demonstrates Lumen's three extension points, each implemented entirely in app code:
 *
 * 1. `CoachmarkConfig.progressIndicator` — a pill-style step indicator, no library enum needed.
 * 2. `CoachmarkConfig.tooltipShape` — a speech-bubble tail carved into the card's own outline.
 * 3. `CutoutShape.Custom` — a diamond cutout that isn't one of the built-in shapes.
 *
 * The point of this example is that none of these required a change to `:lumen`. Anything
 * app- or brand-specific belongs here, not in the library's public API.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRenderingExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val controller = rememberCoachmarkController()
    var started by remember { mutableStateOf(false) }
    val density = androidx.compose.ui.platform.LocalDensity.current

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            showTooltipCard = true,
            scrimTapBehavior = ScrimTapBehavior.NONE,
            // 1. Pill progress indicator — the "current step is a pill, others are dots" look,
            //    built from ordinary Compose in app code.
            progressIndicator = { currentStep, totalSteps ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(totalSteps) { index ->
                        val isCurrent = index == currentStep - 1
                        Box(
                            modifier = Modifier
                                .size(width = if (isCurrent) 20.dp else 8.dp, height = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isCurrent) Color.White else Color.White.copy(alpha = 0.35f),
                                ),
                        )
                    }
                }
            },
            // 2. Speech-bubble tooltip shape — the tail is carved into the card's own
            //    outline (one continuous shape, no connector line at all), pointing at
            //    wherever the target actually sits along the card's edge.
            tooltipShape = { tailAnchorX, isTooltipBelow ->
                SpeechBubbleShape(
                    cornerRadius = with(density) { 16.dp.toPx() },
                    tailWidth = with(density) { 20.dp.toPx() },
                    tailHeight = with(density) { 10.dp.toPx() },
                    tailAnchorX = with(density) { tailAnchorX.toPx() },
                    // isTooltipBelow=true means the tooltip sits below the target, so the
                    // tail must point UP at it — on the card's TOP edge, not bottom.
                    tailOnBottom = !isTooltipBelow,
                )
            },
            tooltipTailInset = 10.dp,
        ),
        colors = coachmarkColors(),
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Custom Rendering") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "App-Owned Rendering",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Pill progress, a speech-bubble tail shape, and a diamond cutout — " +
                        "all built in app code, with no new enum cases in the library.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    IconBadge(Icons.Default.Search, "search", controller)
                    IconBadge(Icons.Default.Notifications, "alerts", controller)
                    IconBadge(Icons.Default.Settings, "settings", controller)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = { started = true; controller.showSequence(customTour()) }) {
                    Text(if (started) "Restart Tour" else "Start Tour")
                }
            }
        }
    }
}

private fun customTour(): List<CoachmarkTarget> = listOf(
    CoachmarkTarget(
        id = "search",
        title = "Pill Progress",
        description = "The step indicator below is a pill, supplied through " +
            "CoachmarkConfig.progressIndicator.",
        shape = CutoutShape.Circle(radiusPadding = 10.dp),
        ctaText = "Next",
    ),
    CoachmarkTarget(
        id = "alerts",
        title = "Speech Bubble Tail",
        description = "The card's own outline has a tail carved into it, via " +
            "CoachmarkConfig.tooltipShape — no separate connector at all.",
        shape = CutoutShape.Circle(radiusPadding = 10.dp),
        // The card's own shape provides the tail; suppress the built-in line entirely by
        // opting into CUSTOM with a no-op renderer (there's no ConnectorStyle.NONE today).
        connectorStyle = ConnectorStyle.CUSTOM,
        customConnector = { _, _, _ -> },
        ctaText = "Next",
    ),
    CoachmarkTarget(
        id = "settings",
        title = "Diamond Cutout",
        description = "A shape that isn't built in, supplied through CutoutShape.Custom.",
        shape = CutoutShape.Custom { bounds: Rect, density: Density ->
            val inset = with(density) { 10.dp.toPx() }
            Path().apply {
                moveTo(bounds.center.x, bounds.top - inset)
                lineTo(bounds.right + inset, bounds.center.y)
                lineTo(bounds.center.x, bounds.bottom + inset)
                lineTo(bounds.left - inset, bounds.center.y)
                close()
            }
        },
        ctaText = "Done",
    ),
)

@Composable
private fun IconBadge(
    icon: ImageVector,
    id: String,
    controller: CoachmarkController,
) {
    Card(
        modifier = Modifier
            .size(56.dp)
            .coachmarkTarget(controller, id),
        shape = CircleShape,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = id)
        }
    }
}
