package com.aditlal.sample.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkConfig
import io.luminos.CoachmarkHost
import io.luminos.CoachmarkTarget
import io.luminos.CutoutShape
import io.luminos.HighlightAnimation
import io.luminos.TargetTapBehavior
import io.luminos.coachmarkColors
import io.luminos.coachmarkTarget
import io.luminos.rememberCoachmarkController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TapThroughExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val controller = rememberCoachmarkController()
    var showCoachmark by remember { mutableStateOf(false) }
    var lastTappedTarget by remember { mutableStateOf<String?>(null) }

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            highlightAnimation = HighlightAnimation.PULSE,
        ),
        colors = coachmarkColors(),
        onTargetTap = { targetId ->
            lastTappedTarget = targetId
        },
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Tap-Through Behavior") },
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
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Target Tap Behavior",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Each button has a different tap behavior on the cutout area",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(48.dp)
                                .coachmarkTarget(controller, "pass-through"),
                        ) {
                            Icon(Icons.Default.Favorite, "Favorite")
                        }
                        Text("Pass-through", style = MaterialTheme.typography.labelSmall)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(48.dp)
                                .coachmarkTarget(controller, "advance"),
                        ) {
                            Icon(Icons.Default.Notifications, "Notifications")
                        }
                        Text("Advance", style = MaterialTheme.typography.labelSmall)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(48.dp)
                                .coachmarkTarget(controller, "both"),
                        ) {
                            Icon(Icons.Default.Share, "Share")
                        }
                        Text("Both", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                lastTappedTarget?.let {
                    Text(
                        text = "Last tapped target: $it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(onClick = { showCoachmark = true }) {
                    Text("Show Sequence")
                }
            }
        }
    }

    if (showCoachmark) {
        controller.showSequence(
            listOf(
                CoachmarkTarget(
                    id = "pass-through",
                    title = "Pass-Through",
                    description = "Tapping the cutout does nothing. Only the CTA button advances.",
                    shape = CutoutShape.Circle(radiusPadding = 12.dp),
                    targetTapBehavior = TargetTapBehavior.PASS_THROUGH,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "advance",
                    title = "Advance",
                    description = "Tapping the cutout advances to the next step.",
                    shape = CutoutShape.Circle(radiusPadding = 12.dp),
                    targetTapBehavior = TargetTapBehavior.ADVANCE,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "both",
                    title = "Both",
                    description = "Tapping the cutout fires the onTargetTap callback AND advances.",
                    shape = CutoutShape.Circle(radiusPadding = 12.dp),
                    targetTapBehavior = TargetTapBehavior.BOTH,
                    ctaText = "Done",
                ),
            ),
        )
        showCoachmark = false
    }
}
