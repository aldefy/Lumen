package io.luminos.screenshot

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import io.luminos.CoachmarkConfig
import io.luminos.CoachmarkController
import io.luminos.CoachmarkScrim
import io.luminos.CoachmarkTarget
import io.luminos.ConnectorStyle
import io.luminos.CutoutShape
import io.luminos.DarkCoachmarkColors
import io.luminos.HighlightAnimation
import io.luminos.LightCoachmarkColors
import io.luminos.TooltipPosition
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CoachmarkScrimScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val roborazziOptions = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(
            changeThreshold = 0.02f,
        ),
    )

    private val noAnimConfig = CoachmarkConfig(
        fadeAnimationDuration = 0,
        connectorAnimationDuration = 0,
        tooltipAnimationDuration = 0,
        highlightAnimation = HighlightAnimation.NONE,
        waitForVisibility = false,
    )

    private val targetBounds = Rect(left = 150f, top = 200f, right = 250f, bottom = 260f)

    private fun showScrimAndCapture(
        target: CoachmarkTarget,
        config: CoachmarkConfig = noAnimConfig,
        colors: io.luminos.CoachmarkColors = LightCoachmarkColors,
        darkTheme: Boolean = false,
        filePath: String,
    ) {
        val controller = CoachmarkController()

        composeTestRule.setContent {
            MaterialTheme(colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()) {
                CoachmarkScrim(
                    controller = controller,
                    config = config,
                    colors = colors,
                )
            }
        }

        composeTestRule.runOnIdle {
            controller.show(target)
        }

        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()

        composeTestRule.onRoot().captureRoboImage(
            filePath = filePath,
            roborazziOptions = roborazziOptions,
        )
    }

    @Test
    fun scrim_circleCutout() {
        showScrimAndCapture(
            target = CoachmarkTarget(
                id = "circle",
                bounds = targetBounds,
                shape = CutoutShape.Circle(),
                title = "Circle Cutout",
                description = "A circular highlight around the target element.",
                connectorStyle = ConnectorStyle.VERTICAL,
            ),
            filePath = "src/androidUnitTest/snapshots/scrim_circleCutout.png",
        )
    }

    @Test
    fun scrim_roundedRectCutout() {
        showScrimAndCapture(
            target = CoachmarkTarget(
                id = "roundedrect",
                bounds = targetBounds,
                shape = CutoutShape.RoundedRect(),
                title = "Rounded Rect",
                description = "A rounded rectangle cutout shape.",
                connectorStyle = ConnectorStyle.VERTICAL,
            ),
            filePath = "src/androidUnitTest/snapshots/scrim_roundedRectCutout.png",
        )
    }

    @Test
    fun scrim_rectCutout() {
        showScrimAndCapture(
            target = CoachmarkTarget(
                id = "rect",
                bounds = targetBounds,
                shape = CutoutShape.Rect(),
                title = "Sharp Rectangle",
                description = "A sharp-edged rectangular cutout.",
                connectorStyle = ConnectorStyle.VERTICAL,
            ),
            filePath = "src/androidUnitTest/snapshots/scrim_rectCutout.png",
        )
    }

    @Test
    fun scrim_squircleCutout() {
        showScrimAndCapture(
            target = CoachmarkTarget(
                id = "squircle",
                bounds = targetBounds,
                shape = CutoutShape.Squircle(),
                title = "Squircle Shape",
                description = "An iOS-style superellipse cutout.",
                connectorStyle = ConnectorStyle.VERTICAL,
            ),
            filePath = "src/androidUnitTest/snapshots/scrim_squircleCutout.png",
        )
    }

    @Test
    fun scrim_starCutout() {
        showScrimAndCapture(
            target = CoachmarkTarget(
                id = "star",
                bounds = targetBounds,
                shape = CutoutShape.Star(points = 5),
                title = "Star Shape",
                description = "A five-pointed star cutout for gamification.",
                connectorStyle = ConnectorStyle.VERTICAL,
            ),
            filePath = "src/androidUnitTest/snapshots/scrim_starCutout.png",
        )
    }

    @Test
    fun scrim_elbowConnector() {
        showScrimAndCapture(
            target = CoachmarkTarget(
                id = "elbow",
                bounds = targetBounds,
                shape = CutoutShape.Circle(),
                title = "Elbow Connector",
                description = "An L-shaped connector line from cutout to tooltip.",
                connectorStyle = ConnectorStyle.ELBOW,
            ),
            filePath = "src/androidUnitTest/snapshots/scrim_elbowConnector.png",
        )
    }

    @Test
    fun scrim_directConnector() {
        showScrimAndCapture(
            target = CoachmarkTarget(
                id = "direct",
                bounds = targetBounds,
                shape = CutoutShape.Circle(),
                title = "Direct Connector",
                description = "A diagonal connector line from cutout to tooltip.",
                connectorStyle = ConnectorStyle.DIRECT,
            ),
            filePath = "src/androidUnitTest/snapshots/scrim_directConnector.png",
        )
    }

    @Test
    fun scrim_customConnector_targetOverride() {
        // A distinctive filled triangle proves the per-target lambda actually renders in
        // place of the built-in line/endpoint, and that it takes priority over the
        // config-level default (set below to a different, unmistakable color).
        showScrimAndCapture(
            target = CoachmarkTarget(
                id = "customTarget",
                bounds = targetBounds,
                shape = CutoutShape.Circle(),
                title = "Custom Connector (per-target)",
                description = "Rendered by CoachmarkTarget.customConnector, not the config default.",
                connectorStyle = ConnectorStyle.CUSTOM,
                customConnector = { from, to, progress ->
                    val tip = androidx.compose.ui.geometry.Offset(
                        x = to.x + (from.x - to.x) * progress,
                        y = to.y + (from.y - to.y) * progress,
                    )
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(to.x - 16f, to.y)
                            lineTo(to.x + 16f, to.y)
                            lineTo(tip.x, tip.y)
                            close()
                        },
                        color = androidx.compose.ui.graphics.Color.Magenta,
                    )
                },
            ),
            config = noAnimConfig.copy(
                customConnector = { _, to, _ ->
                    // Should never render: the target-level lambda above wins.
                    drawCircle(color = androidx.compose.ui.graphics.Color.Red, radius = 40f, center = to)
                },
            ),
            filePath = "src/androidUnitTest/snapshots/scrim_customConnectorTargetOverride.png",
        )
    }

    @Test
    fun scrim_customConnector_missingLambdaFallsBackToLine() {
        // CUSTOM with no lambda supplied anywhere should draw a normal connector line
        // (as if AUTO had been requested) rather than nothing at all.
        showScrimAndCapture(
            target = CoachmarkTarget(
                id = "customFallback",
                bounds = targetBounds,
                shape = CutoutShape.Circle(),
                title = "Custom Connector (fallback)",
                description = "No customConnector lambda supplied; falls back to a normal line.",
                connectorStyle = ConnectorStyle.CUSTOM,
            ),
            filePath = "src/androidUnitTest/snapshots/scrim_customConnectorFallback.png",
        )
    }

    @Test
    fun scrim_tooltipAbove() {
        val lowerTarget = Rect(left = 150f, top = 600f, right = 250f, bottom = 660f)
        showScrimAndCapture(
            target = CoachmarkTarget(
                id = "above",
                bounds = lowerTarget,
                shape = CutoutShape.Circle(),
                title = "Tooltip Above",
                description = "The tooltip is positioned above the target.",
                tooltipPosition = TooltipPosition.TOP,
                connectorStyle = ConnectorStyle.VERTICAL,
            ),
            filePath = "src/androidUnitTest/snapshots/scrim_tooltipAbove.png",
        )
    }

    @Test
    fun scrim_darkTheme() {
        showScrimAndCapture(
            target = CoachmarkTarget(
                id = "dark",
                bounds = targetBounds,
                shape = CutoutShape.Circle(),
                title = "Dark Theme",
                description = "Scrim rendered with dark coachmark colors.",
                connectorStyle = ConnectorStyle.VERTICAL,
            ),
            colors = DarkCoachmarkColors,
            darkTheme = true,
            filePath = "src/androidUnitTest/snapshots/scrim_darkTheme.png",
        )
    }

    @Test
    fun scrim_cardMode() {
        showScrimAndCapture(
            target = CoachmarkTarget(
                id = "card",
                bounds = targetBounds,
                shape = CutoutShape.RoundedRect(),
                title = "Card Mode",
                description = "Tooltip wrapped in a card background.",
                connectorStyle = ConnectorStyle.VERTICAL,
            ),
            config = noAnimConfig.copy(showTooltipCard = true),
            filePath = "src/androidUnitTest/snapshots/scrim_cardMode.png",
        )
    }
}
