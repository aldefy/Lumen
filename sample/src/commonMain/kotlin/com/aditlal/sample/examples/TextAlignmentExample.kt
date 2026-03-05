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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Call
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
import androidx.compose.ui.text.style.TextAlign
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
fun TextAlignmentExample(
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
                    title = { Text("Text Alignment") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Text Alignment & Inline Title",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Simulates tooltip above/below targets in a scrollable list",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = { showSequence = true }) {
                        Text("Show Text Alignment Demo")
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Target near top — tooltip will appear BELOW (inline dot works)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .coachmarkTarget(controller, "top_target"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Top target",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Spacer items to push next target toward bottom
                items(8) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                        )
                        Spacer(modifier = Modifier.padding(start = 12.dp))
                        Column {
                            Text(
                                text = "Contact ${index + 1}",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = "List item to fill space",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                // Target near bottom — tooltip will appear ABOVE (tests inline title fallback)
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.errorContainer)
                                .coachmarkTarget(controller, "bottom_target"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "Bottom target (muted)",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Center-aligned target
                item {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .coachmarkTarget(controller, "center_aligned"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Center aligned",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    if (showSequence) {
        controller.showSequence(
            listOf(
                // Step 1: Target near top — tooltip BELOW — inline title active (dot at top)
                CoachmarkTarget(
                    id = "top_target",
                    title = "Muted Call",
                    description = "This call has been muted for you. Inline title with dot beside it.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 56.dp,
                    tooltipTextAlign = TextAlign.Center,
                    titleInlineWithConnector = true,
                    ctaText = "Next",
                ),
                // Step 2: Target near bottom — tooltip ABOVE — inline title should gracefully fallback
                CoachmarkTarget(
                    id = "bottom_target",
                    title = "Muted Call",
                    description = "Tooltip is above the target. Inline title should fall back to standard layout since dot is at bottom.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 56.dp,
                    tooltipTextAlign = TextAlign.Center,
                    titleInlineWithConnector = true,
                    ctaText = "Next",
                ),
                // Step 3: Center-aligned only (no inline title)
                CoachmarkTarget(
                    id = "center_aligned",
                    title = "Center Aligned",
                    description = "Plain center-aligned text without inline title.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 56.dp,
                    tooltipTextAlign = TextAlign.Center,
                    ctaText = "Got it!",
                ),
            )
        )
        showSequence = false
    }
}
