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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Text Alignment & Inline Title",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Center-aligned tooltip text and title inline with connector",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Default (start-aligned) target
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .coachmarkTarget(controller, "start_aligned"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Start aligned",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Center-aligned target
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

                // Inline title target
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .coachmarkTarget(controller, "inline_title"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Inline title",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(onClick = { showSequence = true }) {
                    Text("Show Text Alignment Demo")
                }
            }
        }
    }

    if (showSequence) {
        controller.showSequence(
            listOf(
                CoachmarkTarget(
                    id = "start_aligned",
                    title = "Start Aligned (Default)",
                    description = "This tooltip uses the default start-aligned text. Title and description are left-aligned as usual.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 80.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "center_aligned",
                    title = "Center Aligned",
                    description = "This tooltip text is center-aligned for a cleaner, more balanced look when the tooltip is centered on screen.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 80.dp,
                    tooltipTextAlign = TextAlign.Center,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "inline_title",
                    title = "Inline Title",
                    description = "The title sits beside the connector dot on the same line, creating a compact layout that connects the title visually to the target.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 80.dp,
                    titleInlineWithConnector = true,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "center_aligned",
                    title = "Both Combined",
                    description = "Center-aligned text combined with an inline title creates a polished tooltip that feels well-connected and balanced.",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 80.dp,
                    tooltipTextAlign = TextAlign.Center,
                    titleInlineWithConnector = true,
                    ctaText = "Got it!",
                ),
            )
        )
        showSequence = false
    }
}
