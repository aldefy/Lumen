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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.luminos.BackPressBehavior
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
fun SequenceExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val controller = rememberCoachmarkController()
    var showSequence by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            highlightAnimation = HighlightAnimation.PULSE,
            showSkipButton = true,
            skipButtonText = "Skip tour",
            backPressBehavior = BackPressBehavior.NAVIGATE,
            scrimTapBehavior = ScrimTapBehavior.NONE,
        ),
        colors = coachmarkColors(),
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Multi-Step Sequence") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.coachmarkTarget(controller, "search")
                        ) {
                            Icon(Icons.Default.Search, "Search")
                        }
                        IconButton(
                            onClick = { },
                            modifier = Modifier.coachmarkTarget(controller, "add")
                        ) {
                            Icon(Icons.Default.Add, "Add")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                "Home",
                                modifier = Modifier.coachmarkTarget(controller, "home")
                            )
                        },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                Icons.Default.Person,
                                "Profile",
                                modifier = Modifier.coachmarkTarget(controller, "profile")
                            )
                        },
                        label = { Text("Profile") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                Icons.Default.Settings,
                                "Settings",
                                modifier = Modifier.coachmarkTarget(controller, "settings")
                            )
                        },
                        label = { Text("Settings") }
                    )
                }
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
                    text = "Multi-Step Sequence",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "5-step onboarding tour with progress indicator.\nPress back to navigate between steps.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(onClick = { showSequence = true }) {
                        Text("Start Tour")
                    }

                    Button(onClick = { controller.dismiss() }) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }

    if (showSequence) {
        controller.showSequence(
            listOf(
                CoachmarkTarget(
                    id = "home",
                    title = "Home",
                    description = "Your main dashboard. See all your recent activity here.",
                    shape = CutoutShape.Circle(radiusPadding = 8.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 60.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "profile",
                    title = "Profile",
                    description = "View and edit your account details and preferences.",
                    shape = CutoutShape.Circle(radiusPadding = 8.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 60.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "settings",
                    title = "Settings",
                    description = "Customize the app behavior and appearance.",
                    shape = CutoutShape.Circle(radiusPadding = 8.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 60.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "search",
                    title = "Search",
                    description = "Find anything in the app quickly with powerful search.",
                    shape = CutoutShape.Circle(radiusPadding = 8.dp),
                    connectorStyle = ConnectorStyle.ELBOW,
                    connectorLength = 80.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "add",
                    title = "Create New",
                    description = "Tap here to create new items or content.",
                    shape = CutoutShape.Circle(radiusPadding = 8.dp),
                    connectorStyle = ConnectorStyle.ELBOW,
                    connectorLength = 80.dp,
                    ctaText = "Got it!",
                ),
            )
        )
        showSequence = false
    }
}
