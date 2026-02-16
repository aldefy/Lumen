package com.aditlal.sample.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import io.luminos.CoachmarkConfig
import io.luminos.CoachmarkHost
import io.luminos.CoachmarkTarget
import io.luminos.ConnectorEndStyle
import io.luminos.ConnectorStyle
import io.luminos.CutoutShape
import io.luminos.HighlightAnimation
import io.luminos.ScrimTapBehavior
import io.luminos.coachmarkColors
import io.luminos.coachmarkTarget
import io.luminos.rememberCoachmarkController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectorsExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val controller = rememberCoachmarkController()
    var showSequence by remember { mutableStateOf(false) }

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            highlightAnimation = HighlightAnimation.GLOW,
            showSkipButton = true,
            skipButtonText = "Skip",
            scrimTapBehavior = ScrimTapBehavior.NONE,
            customConnectorEnd = { center, angle ->
                // Diamond shape endpoint
                val size = 10f
                val path = Path()
                path.moveTo(center.x + size * cos(angle), center.y + size * sin(angle))
                path.lineTo(
                    center.x + size * cos(angle + PI.toFloat() / 2f),
                    center.y + size * sin(angle + PI.toFloat() / 2f),
                )
                path.lineTo(
                    center.x + size * cos(angle + PI.toFloat()),
                    center.y + size * sin(angle + PI.toFloat()),
                )
                path.lineTo(
                    center.x + size * cos(angle - PI.toFloat() / 2f),
                    center.y + size * sin(angle - PI.toFloat() / 2f),
                )
                path.close()
                drawPath(path = path, color = Color.White)
            },
        ),
        colors = coachmarkColors(),
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Connector Styles") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.coachmarkTarget(controller, "notifications")
                        ) {
                            Icon(Icons.Default.Notifications, "Notifications")
                        }
                        IconButton(
                            onClick = { },
                            modifier = Modifier.coachmarkTarget(controller, "settings")
                        ) {
                            Icon(Icons.Default.Settings, "Settings")
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
                    text = "Connector Styles",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Eight different ways to connect tooltips to targets",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Left aligned target for horizontal connector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .coachmarkTarget(controller, "horizontal"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Horizontal",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Center aligned target for vertical connector
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .coachmarkTarget(controller, "vertical"),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Vertical",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Right aligned target for direct connector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .coachmarkTarget(controller, "direct"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Direct",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(onClick = { showSequence = true }) {
                    Text("Show All Connectors")
                }
            }
        }
    }

    if (showSequence) {
        controller.showSequence(
            listOf(
                CoachmarkTarget(
                    id = "vertical",
                    title = "Vertical Connector",
                    description = "Draws a straight line vertically from the target. Best for elements in the center of the screen.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 80.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "horizontal",
                    title = "Horizontal Connector",
                    description = "Draws a straight line horizontally from the target. Best for elements at the edges of the screen.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.HORIZONTAL,
                    connectorLength = 100.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "notifications",
                    title = "Elbow Connector",
                    description = "Creates an L-shaped connector with a 90° bend. Perfect for corner elements and app bar icons.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.ELBOW,
                    connectorLength = 90.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "direct",
                    title = "Direct Connector",
                    description = "Draws a straight diagonal line directly to the tooltip. Creates a clean, minimal look.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.DIRECT,
                    connectorLength = 80.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "horizontal",
                    title = "Arrow Connector",
                    description = "Arrowhead endpoint pointing toward the tooltip for clear directionality.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.DIRECT,
                    connectorEndStyle = ConnectorEndStyle.ARROW,
                    connectorLength = 80.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "vertical",
                    title = "Curved Connector",
                    description = "Smooth Bezier curve for an elegant flowing line.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.CURVED,
                    connectorLength = 80.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "settings",
                    title = "Auto Connector",
                    description = "Automatically picks the best connector style based on target position. Recommended for most use cases.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.AUTO,
                    connectorLength = 90.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "notifications",
                    title = "Custom Endpoint",
                    description = "Provide a DrawScope lambda for fully custom endpoint rendering.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.ELBOW,
                    connectorEndStyle = ConnectorEndStyle.CUSTOM,
                    connectorLength = 90.dp,
                    ctaText = "Got it!",
                ),
            )
        )
        showSequence = false
    }
}
