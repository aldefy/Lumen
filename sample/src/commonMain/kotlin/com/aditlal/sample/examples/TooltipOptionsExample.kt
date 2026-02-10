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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import io.luminos.ScrimTapBehavior
import io.luminos.coachmarkColors
import io.luminos.coachmarkTarget
import io.luminos.rememberCoachmarkController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipOptionsExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val controller = rememberCoachmarkController()
    var showCoachmark by remember { mutableStateOf(false) }

    // Configuration options
    var showTooltipCard by remember { mutableStateOf(false) }
    var showSkipButton by remember { mutableStateOf(false) }
    var customCtaText by remember { mutableStateOf(false) }

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            highlightAnimation = HighlightAnimation.PULSE,
            scrimTapBehavior = ScrimTapBehavior.NONE,
            showTooltipCard = showTooltipCard,
            showSkipButton = showSkipButton,
            skipButtonText = "Skip Tour",
        ),
        colors = coachmarkColors(),
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Tooltip Options") },
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
                    text = "Tooltip Customization",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Configure tooltip appearance and interaction options",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Target
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .coachmarkTarget(controller, "target"),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Target",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(36.dp),
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Configuration options
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OptionRow(
                        label = "Show Tooltip Card",
                        description = "Wrap tooltip in a card container",
                        checked = showTooltipCard,
                        onCheckedChange = { showTooltipCard = it }
                    )

                    OptionRow(
                        label = "Show Skip Button",
                        description = "Add skip button to dismiss tour",
                        checked = showSkipButton,
                        onCheckedChange = { showSkipButton = it }
                    )

                    OptionRow(
                        label = "Custom CTA Text",
                        description = "Change button from 'Got it!' to custom",
                        checked = customCtaText,
                        onCheckedChange = { customCtaText = it }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedButton(onClick = { showCoachmark = true }) {
                    Text("Show Tooltip")
                }
            }
        }
    }

    if (showCoachmark) {
        controller.show(
            CoachmarkTarget(
                id = "target",
                title = "Tooltip Options Demo",
                description = buildString {
                    append("Card: ${if (showTooltipCard) "ON" else "OFF"}")
                    append(" • Skip: ${if (showSkipButton) "ON" else "OFF"}")
                    append(" • CTA: ${if (customCtaText) "Custom" else "Default"}")
                },
                shape = CutoutShape.Circle(radiusPadding = 16.dp),
                connectorStyle = ConnectorStyle.VERTICAL,
                connectorLength = 80.dp,
                ctaText = if (customCtaText) "Awesome!" else "Got it!",
            )
        )
        showCoachmark = false
    }
}

@Composable
private fun OptionRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}
