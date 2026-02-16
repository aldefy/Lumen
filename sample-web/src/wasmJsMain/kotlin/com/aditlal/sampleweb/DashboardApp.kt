package com.aditlal.sampleweb

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aditlal.sampleweb.theme.DashboardTheme
import com.aditlal.sampleweb.tour.dashboardTourTargets
import io.luminos.BackPressBehavior
import io.luminos.CoachmarkConfig
import io.luminos.CoachmarkHost
import io.luminos.HighlightAnimation
import io.luminos.ScrimTapBehavior
import io.luminos.coachmarkColors
import io.luminos.rememberCoachmarkController
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun DashboardApp() {
    DashboardTheme {
        val controller = rememberCoachmarkController()
        var startTour by remember { mutableStateOf(false) }
        var hasAutoStarted by remember { mutableStateOf(false) }

        CoachmarkHost(
            controller = controller,
            config = CoachmarkConfig(
                showSkipButton = true,
                showProgressIndicator = true,
                scrimTapBehavior = ScrimTapBehavior.NONE,
                backPressBehavior = BackPressBehavior.NAVIGATE,
                showTooltipCard = true,
                highlightAnimation = HighlightAnimation.NONE,
                connectorCutoutGap = 16.dp,
            ),
            colors = coachmarkColors(darkTheme = true),
        ) {
            DashboardContent(
                controller = controller,
                onStartTour = { startTour = true },
            )
        }

        // Auto-start tour on first load
        LaunchedEffect(Unit) {
            if (!hasAutoStarted) {
                delay(800)
                hasAutoStarted = true
                startTour = true
            }
        }

        if (startTour) {
            controller.showSequence(dashboardTourTargets())
            startTour = false
        }
    }
}
