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
