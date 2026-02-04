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
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkColors
import io.luminos.CoachmarkConfig
import io.luminos.CoachmarkHost
import io.luminos.CoachmarkTarget
import io.luminos.ConnectorStyle
import io.luminos.CutoutShape
import io.luminos.HighlightAnimation
import io.luminos.coachmarkTarget
import io.luminos.rememberCoachmarkController

private enum class ThemePreset(val label: String) {
    DEFAULT("Default"),
    BLUE("Blue"),
    GREEN("Green"),
    ORANGE("Orange"),
    PURPLE("Purple"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemingExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val controller = rememberCoachmarkController()
    var showCoachmark by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(ThemePreset.DEFAULT) }

    val colors = when (selectedTheme) {
        ThemePreset.DEFAULT -> CoachmarkColors(
            scrimColor = Color.Black.copy(alpha = 0.85f),
            strokeColor = Color.White,
            tooltipBackground = Color.White,
            titleColor = Color.Black,
            descriptionColor = Color.DarkGray,
            ctaButtonColor = Color(0xFF007AFF),
            ctaTextColor = Color.White,
        )
        ThemePreset.BLUE -> CoachmarkColors(
            scrimColor = Color(0xFF1A237E).copy(alpha = 0.9f),
            strokeColor = Color(0xFF64B5F6),
            tooltipBackground = Color(0xFF0D47A1),
            titleColor = Color.White,
            descriptionColor = Color(0xFFBBDEFB),
            ctaButtonColor = Color(0xFF2196F3),
            ctaTextColor = Color.White,
            connectorColor = Color(0xFF64B5F6),
        )
        ThemePreset.GREEN -> CoachmarkColors(
            scrimColor = Color(0xFF1B5E20).copy(alpha = 0.9f),
            strokeColor = Color(0xFF81C784),
            tooltipBackground = Color(0xFF2E7D32),
            titleColor = Color.White,
            descriptionColor = Color(0xFFC8E6C9),
            ctaButtonColor = Color(0xFF4CAF50),
            ctaTextColor = Color.White,
            connectorColor = Color(0xFF81C784),
        )
        ThemePreset.ORANGE -> CoachmarkColors(
            scrimColor = Color(0xFFE65100).copy(alpha = 0.9f),
            strokeColor = Color(0xFFFFB74D),
            tooltipBackground = Color(0xFFBF360C),
            titleColor = Color.White,
            descriptionColor = Color.White.copy(alpha = 0.9f),
            ctaButtonColor = Color(0xFFFF9800),
            ctaTextColor = Color.Black,
            connectorColor = Color(0xFFFFB74D),
        )
        ThemePreset.PURPLE -> CoachmarkColors(
            scrimColor = Color(0xFF4A148C).copy(alpha = 0.9f),
            strokeColor = Color(0xFFBA68C8),
            tooltipBackground = Color(0xFF4A148C),
            titleColor = Color.White,
            descriptionColor = Color.White.copy(alpha = 0.9f),
            ctaButtonColor = Color(0xFF9C27B0),
            ctaTextColor = Color.White,
            connectorColor = Color(0xFFBA68C8),
        )
    }

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            highlightAnimation = HighlightAnimation.PULSE,
        ),
        colors = colors,
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Theming & Colors") },
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
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Custom Theming",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Fully customizable colors for scrim, tooltip, buttons, and connectors",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(48.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .coachmarkTarget(controller, "target"),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Target",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(36.dp),
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Select a theme preset:",
                    style = MaterialTheme.typography.labelLarge,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ThemePreset.entries.take(3).forEach { preset ->
                            ThemeButton(
                                label = preset.label,
                                isSelected = selectedTheme == preset,
                                onClick = { selectedTheme = preset }
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ThemePreset.entries.drop(3).forEach { preset ->
                            ThemeButton(
                                label = preset.label,
                                isSelected = selectedTheme == preset,
                                onClick = { selectedTheme = preset }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(onClick = { showCoachmark = true }) {
                    Text("Show ${selectedTheme.label} Theme")
                }
            }
        }
    }

    if (showCoachmark) {
        controller.show(
            CoachmarkTarget(
                id = "target",
                title = "${selectedTheme.label} Theme",
                description = "All colors are customizable: scrim, stroke, tooltip background, text colors, button colors, and connector.",
                shape = CutoutShape.Circle(radiusPadding = 16.dp),
                connectorStyle = ConnectorStyle.VERTICAL,
                connectorLength = 80.dp,
            )
        )
        showCoachmark = false
    }
}

@Composable
private fun ThemeButton(
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
