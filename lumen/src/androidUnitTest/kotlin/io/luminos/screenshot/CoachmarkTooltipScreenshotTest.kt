package io.luminos.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import io.luminos.CoachmarkTooltip
import io.luminos.DarkCoachmarkColors
import io.luminos.LightCoachmarkColors
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CoachmarkTooltipScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tooltip_singleStep_light() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Welcome",
                        description = "Tap here to get started with the app.",
                        ctaText = "Got it!",
                        currentStep = 1,
                        totalSteps = 1,
                        colors = LightCoachmarkColors,
                        onCtaClick = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            filePath = "src/androidUnitTest/snapshots/tooltip_singleStep_light.png"
        )
    }

    @Test
    fun tooltip_multiStep_step2of4() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Step 2",
                        description = "This is the second step in a four-step sequence.",
                        ctaText = "Next",
                        currentStep = 2,
                        totalSteps = 4,
                        colors = LightCoachmarkColors,
                        onCtaClick = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            filePath = "src/androidUnitTest/snapshots/tooltip_multiStep_step2of4.png"
        )
    }

    @Test
    fun tooltip_withSkipButton() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Pro Tip",
                        description = "You can skip this tutorial at any time.",
                        ctaText = "Next",
                        currentStep = 1,
                        totalSteps = 3,
                        colors = LightCoachmarkColors,
                        showSkipButton = true,
                        onCtaClick = {},
                        onSkipClick = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            filePath = "src/androidUnitTest/snapshots/tooltip_withSkipButton.png"
        )
    }

    @Test
    fun tooltip_cardMode_light() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Card Tooltip",
                        description = "This tooltip has a card background with rounded corners.",
                        ctaText = "Got it!",
                        currentStep = 1,
                        totalSteps = 3,
                        colors = LightCoachmarkColors,
                        showCard = true,
                        onCtaClick = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            filePath = "src/androidUnitTest/snapshots/tooltip_cardMode_light.png"
        )
    }

    @Test
    fun tooltip_cardMode_dark() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Dark Card",
                        description = "Card mode with dark theme colors.",
                        ctaText = "Next",
                        currentStep = 2,
                        totalSteps = 5,
                        colors = DarkCoachmarkColors,
                        showCard = true,
                        onCtaClick = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            filePath = "src/androidUnitTest/snapshots/tooltip_cardMode_dark.png"
        )
    }

    @Test
    fun tooltip_singleStep_dark() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Dark Theme",
                        description = "This tooltip uses dark theme colors without a card.",
                        ctaText = "Got it!",
                        currentStep = 1,
                        totalSteps = 1,
                        colors = DarkCoachmarkColors,
                        onCtaClick = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            filePath = "src/androidUnitTest/snapshots/tooltip_singleStep_dark.png"
        )
    }

    @Test
    fun tooltip_noProgressIndicator() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "No Dots",
                        description = "Progress indicator is explicitly hidden.",
                        ctaText = "Continue",
                        currentStep = 2,
                        totalSteps = 4,
                        colors = LightCoachmarkColors,
                        showProgressIndicator = false,
                        onCtaClick = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            filePath = "src/androidUnitTest/snapshots/tooltip_noProgressIndicator.png"
        )
    }

    @Test
    fun tooltip_skipButton_cardMode_dark() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Full Combo",
                        description = "Skip button, card mode, and dark theme combined.",
                        ctaText = "Next",
                        currentStep = 3,
                        totalSteps = 5,
                        colors = DarkCoachmarkColors,
                        showCard = true,
                        showSkipButton = true,
                        onCtaClick = {},
                        onSkipClick = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            filePath = "src/androidUnitTest/snapshots/tooltip_skipButton_cardMode_dark.png"
        )
    }
}
