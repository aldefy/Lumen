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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

private enum class AlignOption(val label: String, val textAlign: TextAlign) {
    START("Start", TextAlign.Start),
    CENTER("Center", TextAlign.Center),
    END("End", TextAlign.End),
}

private enum class CtaAlignOption(val label: String, val arrangement: Arrangement.Horizontal) {
    START("Start", Arrangement.Start),
    CENTER("Center", Arrangement.Center),
    END("End", Arrangement.End),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextAlignmentExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val controller = rememberCoachmarkController()
    var showDemo by remember { mutableStateOf(false) }

    var titleAlign by remember { mutableStateOf(AlignOption.START) }
    var descAlign by remember { mutableStateOf(AlignOption.START) }
    var skipAlign by remember { mutableStateOf(AlignOption.END) }
    var ctaAlign by remember { mutableStateOf(CtaAlignOption.END) }
    var showSkipButton by remember { mutableStateOf(true) }
    var titleInline by remember { mutableStateOf(true) }

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            highlightAnimation = HighlightAnimation.PULSE,
            showSkipButton = showSkipButton,
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
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Per-Element Text Alignment",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Configure alignment for each tooltip element, then tap Show Demo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Title alignment selector
                AlignSelector(
                    label = "Title",
                    selected = titleAlign,
                    onSelect = { titleAlign = it },
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description alignment selector
                AlignSelector(
                    label = "Description",
                    selected = descAlign,
                    onSelect = { descAlign = it },
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Skip button alignment selector
                AlignSelector(
                    label = "Skip Button",
                    selected = skipAlign,
                    onSelect = { skipAlign = it },
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CTA alignment selector
                CtaAlignSelector(
                    label = "CTA Button",
                    selected = ctaAlign,
                    onSelect = { ctaAlign = it },
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Show Skip Button",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Switch(
                        checked = showSkipButton,
                        onCheckedChange = { showSkipButton = it },
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Title Inline With Connector",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Switch(
                        checked = titleInline,
                        onCheckedChange = { titleInline = it },
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Target
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .coachmarkTarget(controller, "demo_target"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Demo target",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    Spacer(modifier = Modifier.padding(start = 12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sarah Connor", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Last called 2 hours ago",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = { showDemo = true }) {
                    Text("Show Demo")
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    LaunchedEffect(showDemo) {
        if (showDemo) {
            controller.show(
                CoachmarkTarget(
                    id = "demo_target",
                    title = "Quick Call",
                    description = "Tap to start a call. Long press to send a voice message instead.",
                    shape = CutoutShape.Circle(radiusPadding = 12.dp),
                    connectorStyle = ConnectorStyle.ELBOW,
                    connectorLength = 56.dp,
                    titleInlineWithConnector = titleInline,
                    titleTextAlign = titleAlign.textAlign,
                    descriptionTextAlign = descAlign.textAlign,
                    skipButtonTextAlign = skipAlign.textAlign,
                    ctaHorizontalArrangement = ctaAlign.arrangement,
                    ctaText = "Got it!",
                )
            )
            showDemo = false
        }
    }
}

@Composable
private fun AlignSelector(
    label: String,
    selected: AlignOption,
    onSelect: (AlignOption) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AlignOption.entries.forEach { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelect(option) },
                    label = { Text(option.label) },
                )
            }
        }
    }
}

@Composable
private fun CtaAlignSelector(
    label: String,
    selected: CtaAlignOption,
    onSelect: (CtaAlignOption) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CtaAlignOption.entries.forEach { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelect(option) },
                    label = { Text(option.label) },
                )
            }
        }
    }
}
