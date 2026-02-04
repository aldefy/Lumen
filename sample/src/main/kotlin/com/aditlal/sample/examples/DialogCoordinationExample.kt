package com.aditlal.sample.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import io.luminos.DialogOverlayEffect
import io.luminos.HighlightAnimation
import io.luminos.LocalOverlayCoordinator
import io.luminos.coachmarkColors
import io.luminos.coachmarkTarget
import io.luminos.rememberCoachmarkController
import io.luminos.rememberOverlayCoordinator
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogCoordinationExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val overlayCoordinator = rememberOverlayCoordinator()
    val controller = rememberCoachmarkController(overlayCoordinator)
    var showCoachmark by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var demoRunning by remember { mutableStateOf(false) }

    // Auto-show dialog 2 seconds after coachmark starts
    LaunchedEffect(demoRunning) {
        if (demoRunning) {
            delay(2000)
            showDialog = true
            demoRunning = false
        }
    }

    CompositionLocalProvider(LocalOverlayCoordinator provides overlayCoordinator) {
        CoachmarkHost(
            controller = controller,
            config = CoachmarkConfig(
                highlightAnimation = HighlightAnimation.PULSE,
            ),
            colors = coachmarkColors(),
        ) {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = { Text("Dialog Coordination") },
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
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )

                    Text(
                        text = "Dialog Coordination",
                        style = MaterialTheme.typography.headlineSmall,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Coachmarks automatically dismiss when dialogs appear, preventing UI conflicts.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "A coachmark will appear, then a dialog will auto-open after 2 seconds. Watch the coachmark dismiss automatically!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            showCoachmark = true
                            demoRunning = true
                        }
                    ) {
                        Text("Start Demo")
                    }
                }
            }
        }

        // Dialog with overlay coordination
        if (showDialog) {
            DialogOverlayEffect()
            AlertDialog(
                onDismissRequest = { showDialog = false },
                icon = { Icon(Icons.Default.Info, contentDescription = null) },
                title = { Text("Auto-Dismiss Works!") },
                text = {
                    Text("The coachmark was automatically dismissed when this dialog appeared. That's the OverlayCoordinator in action!")
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Got it")
                    }
                },
            )
        }

        if (showCoachmark) {
            controller.show(
                CoachmarkTarget(
                    id = "info",
                    title = "Info Button",
                    description = "Watch what happens when a dialog appears...",
                    shape = CutoutShape.Circle(radiusPadding = 10.dp),
                    connectorStyle = ConnectorStyle.ELBOW,
                    connectorLength = 80.dp,
                )
            )
            showCoachmark = false
        }
    }
}
