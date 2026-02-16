package com.aditlal.sample.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkConfig
import io.luminos.CoachmarkHost
import com.aditlal.sample.rememberCoachmarkRepository
import io.luminos.CoachmarkTarget
import io.luminos.CutoutShape
import io.luminos.coachmarkColors
import io.luminos.coachmarkTarget
import io.luminos.rememberCoachmarkController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DontShowAgainExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val repository = rememberCoachmarkRepository()
    val controller = rememberCoachmarkController(repository = repository)
    var triggerSequence by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Tap 'Show Tour' to start") }

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            dontShowAgainText = "Don't show this again",
        ),
        colors = coachmarkColors(),
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("Don't Show Again") },
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
                    text = "Don't Show Again",
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Check the checkbox and the coachmark won't appear next time",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(48.dp)
                                .coachmarkTarget(controller, "feature-a"),
                        ) {
                            Icon(Icons.Default.Favorite, "Feature A")
                        }
                        Text("Feature A", style = MaterialTheme.typography.labelSmall)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(48.dp)
                                .coachmarkTarget(controller, "feature-b"),
                        ) {
                            Icon(Icons.Default.Star, "Feature B")
                        }
                        Text("Feature B", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { triggerSequence = true }) {
                        Text("Show Tour")
                    }
                    Button(onClick = {
                        repository.resetAllCoachmarks()
                        statusText = "All coachmarks reset!"
                    }) {
                        Text("Reset All")
                    }
                }
            }
        }
    }

    if (triggerSequence) {
        val shown = controller.showSequence(
            listOf(
                CoachmarkTarget(
                    id = "feature-a",
                    title = "Feature A",
                    description = "This coachmark has a 'Don't show again' checkbox.",
                    shape = CutoutShape.Circle(radiusPadding = 12.dp),
                    showDontShowAgain = true,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "feature-b",
                    title = "Feature B",
                    description = "This one does NOT have the checkbox — it always shows.",
                    shape = CutoutShape.Circle(radiusPadding = 12.dp),
                    showDontShowAgain = false,
                    ctaText = "Done",
                ),
            ),
        )
        statusText = if (shown) "Tour started!" else "Some targets were suppressed"
        triggerSequence = false
    }
}
