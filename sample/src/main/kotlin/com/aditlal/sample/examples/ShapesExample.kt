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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkConfig
import io.luminos.CoachmarkHost
import io.luminos.CoachmarkTarget
import io.luminos.ConnectorStyle
import io.luminos.CutoutShape
import io.luminos.HighlightAnimation
import io.luminos.ScrimTapBehavior
import io.luminos.coachmarkColors
import io.luminos.coachmarkTarget
import io.luminos.rememberCoachmarkController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShapesExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val controller = rememberCoachmarkController()
    var showSequence by remember { mutableStateOf(false) }

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            highlightAnimation = HighlightAnimation.PULSE,
            showSkipButton = true,
            skipButtonText = "Skip",
            scrimTapBehavior = ScrimTapBehavior.NONE,
        ),
        colors = coachmarkColors(),
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Shapes Showcase") },
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
                    text = "Cutout Shapes",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Five different cutout shapes for various UI elements",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Row 1: Circle and Rounded Rect
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .coachmarkTarget(controller, "circle"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Circle",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Circle", style = MaterialTheme.typography.labelSmall)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(width = 80.dp, height = 56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .coachmarkTarget(controller, "rounded_rect"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "Btn",
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("RoundedRect", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Row 2: Rect and Squircle
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(width = 72.dp, height = 48.dp)
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                                .coachmarkTarget(controller, "rect"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "Rect",
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Rectangle", style = MaterialTheme.typography.labelSmall)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.errorContainer)
                                .coachmarkTarget(controller, "squircle"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "S",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Squircle", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Row 3: Star
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFFFD700), CircleShape)
                            .coachmarkTarget(controller, "star"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Star",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Star", style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = { showSequence = true }) {
                    Text("Show All Shapes")
                }
            }
        }
    }

    if (showSequence) {
        controller.showSequence(
            listOf(
                CoachmarkTarget(
                    id = "circle",
                    title = "Circle Cutout",
                    description = "Perfect for circular icons, avatars, and FABs.",
                    shape = CutoutShape.Circle(radiusPadding = 12.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 70.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "rounded_rect",
                    title = "Rounded Rectangle",
                    description = "Ideal for buttons and cards with rounded corners.",
                    shape = CutoutShape.RoundedRect(cornerRadius = 16.dp, padding = 8.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 70.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "rect",
                    title = "Rectangle",
                    description = "Clean, sharp edges for text fields and list items.",
                    shape = CutoutShape.Rect(padding = 8.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 70.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "squircle",
                    title = "Squircle (iOS-style)",
                    description = "Superellipse shape with smooth, continuous curves.",
                    shape = CutoutShape.Squircle(cornerRadius = 20.dp, padding = 8.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 70.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "star",
                    title = "Star Cutout",
                    description = "Fun shape for achievements, rewards, or gamification!",
                    shape = CutoutShape.Star(points = 5, innerRadiusRatio = 0.5f, padding = 12.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 70.dp,
                    ctaText = "Got it!",
                ),
            )
        )
        showSequence = false
    }
}
