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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkAnalytics
import io.luminos.CoachmarkConfig
import io.luminos.CoachmarkHost
import io.luminos.CoachmarkTarget
import io.luminos.CutoutShape
import io.luminos.coachmarkColors
import io.luminos.coachmarkTarget
import io.luminos.rememberCoachmarkController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val controller = rememberCoachmarkController()
    var showCoachmark by remember { mutableStateOf(false) }
    val events = remember { mutableStateListOf<String>() }

    val analytics = remember {
        CoachmarkAnalytics(
            onShow = { targetId, stepIndex, totalSteps ->
                events.add("onShow: $targetId (step ${stepIndex + 1}/$totalSteps)")
            },
            onDismiss = { targetId, stepIndex, totalSteps, reason ->
                events.add("onDismiss: $targetId (reason=$reason, step ${stepIndex + 1}/$totalSteps)")
            },
            onAdvance = { fromId, toId, stepIndex, totalSteps ->
                events.add("onAdvance: $fromId -> ${toId ?: "end"} (step ${stepIndex + 1}/$totalSteps)")
            },
            onComplete = { totalSteps ->
                events.add("onComplete: $totalSteps steps finished")
            },
        )
    }

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            showSkipButton = true,
            skipButtonText = "Skip tour",
        ),
        colors = coachmarkColors(),
        analytics = analytics,
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Analytics Callbacks") },
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
                    text = "Analytics Callbacks",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Events are logged below as you interact with the coachmark",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(48.dp)
                            .coachmarkTarget(controller, "home"),
                    ) {
                        Icon(Icons.Default.Home, "Home")
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(48.dp)
                            .coachmarkTarget(controller, "search"),
                    ) {
                        Icon(Icons.Default.Search, "Search")
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(48.dp)
                            .coachmarkTarget(controller, "settings"),
                    ) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { showCoachmark = true }) {
                        Text("Start Tour")
                    }
                    Button(onClick = { events.clear() }) {
                        Text("Clear Log")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Event Log:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    events.forEachIndexed { index, event ->
                        Text(
                            text = "${index + 1}. $event",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (events.isEmpty()) {
                        Text(
                            text = "No events yet. Start the tour!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }

    if (showCoachmark) {
        controller.showSequence(
            listOf(
                CoachmarkTarget(
                    id = "home",
                    title = "Home",
                    description = "Navigate to the home screen.",
                    shape = CutoutShape.Circle(radiusPadding = 12.dp),
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "search",
                    title = "Search",
                    description = "Find anything in the app.",
                    shape = CutoutShape.Circle(radiusPadding = 12.dp),
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "settings",
                    title = "Settings",
                    description = "Configure your preferences.",
                    shape = CutoutShape.Circle(radiusPadding = 12.dp),
                    ctaText = "Done",
                ),
            ),
        )
        showCoachmark = false
    }
}
