package com.aditlal.sample.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkConfig
import io.luminos.CoachmarkHost
import io.luminos.CoachmarkTarget
import io.luminos.ConnectorStyle
import io.luminos.CutoutShape
import io.luminos.HighlightAnimation
import io.luminos.ScrimOpacity
import io.luminos.coachmarkColors
import io.luminos.coachmarkTarget
import io.luminos.rememberCoachmarkController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrimOpacityExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    var selectedOpacity by remember { mutableStateOf<ScrimOpacity?>(null) }
    var showCoachmark by remember { mutableStateOf(false) }

    val controller = rememberCoachmarkController()

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            highlightAnimation = HighlightAnimation.PULSE,
            scrimOpacity = selectedOpacity,
        ),
        colors = coachmarkColors(),
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Scrim Opacity") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.coachmarkTarget(controller, "info")
                        ) {
                            Icon(Icons.Default.Info, "Info")
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
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Scrim Opacity Levels",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Control how dark the overlay appears behind the coachmark",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Opacity selection buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OpacityButton(
                            label = "Light (30%)",
                            isSelected = selectedOpacity == ScrimOpacity.LIGHT,
                            onClick = { selectedOpacity = ScrimOpacity.LIGHT }
                        )
                        OpacityButton(
                            label = "Medium (50%)",
                            isSelected = selectedOpacity == ScrimOpacity.MEDIUM,
                            onClick = { selectedOpacity = ScrimOpacity.MEDIUM }
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OpacityButton(
                            label = "Dark (70%)",
                            isSelected = selectedOpacity == ScrimOpacity.DARK,
                            onClick = { selectedOpacity = ScrimOpacity.DARK }
                        )
                        OpacityButton(
                            label = "Extra Dark (85%)",
                            isSelected = selectedOpacity == ScrimOpacity.EXTRA_DARK,
                            onClick = { selectedOpacity = ScrimOpacity.EXTRA_DARK }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Selected: ${selectedOpacity?.name ?: "Default (Extra Dark)"}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showCoachmark = true },
                    enabled = selectedOpacity != null,
                ) {
                    Text("Show Coachmark")
                }
            }
        }
    }

    if (showCoachmark) {
        controller.show(
            CoachmarkTarget(
                id = "info",
                title = "${selectedOpacity?.name ?: "Default"} Opacity",
                description = "This is how the scrim looks at ${selectedOpacity?.alpha?.times(100)?.toInt() ?: 85}% opacity. Notice how the background content is ${if ((selectedOpacity?.alpha ?: 0.85f) > 0.6f) "mostly hidden" else "still visible"}.",
                shape = CutoutShape.Circle(radiusPadding = 10.dp),
                connectorStyle = ConnectorStyle.ELBOW,
                connectorLength = 80.dp,
            )
        )
        showCoachmark = false
    }
}

@Composable
private fun OpacityButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    if (isSelected) {
        Button(onClick = onClick) {
            Text(label)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(label)
        }
    }
}
