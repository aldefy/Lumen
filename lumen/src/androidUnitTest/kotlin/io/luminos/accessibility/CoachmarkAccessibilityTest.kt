package io.luminos.accessibility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkTooltip
import io.luminos.LightCoachmarkColors
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CoachmarkAccessibilityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tooltip_title_is_semantic_heading() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Welcome",
                        description = "Get started here.",
                        ctaText = "Got it!",
                        currentStep = 1,
                        totalSteps = 1,
                        colors = LightCoachmarkColors,
                        onCtaClick = {},
                    )
                }
            }
        }

        composeTestRule
            .onNodeWithText("Welcome")
            .assertIsDisplayed()
            .assert(SemanticsMatcher("is heading") {
                it.config.getOrNull(SemanticsProperties.Heading) != null
            })
    }

    @Test
    fun tooltip_has_combined_content_description() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Feature Tour",
                        description = "Learn about new features.",
                        ctaText = "Next",
                        currentStep = 2,
                        totalSteps = 5,
                        colors = LightCoachmarkColors,
                        onCtaClick = {},
                    )
                }
            }
        }

        composeTestRule
            .onNode(
                SemanticsMatcher("has tooltip content description") {
                    val descriptions = it.config.getOrNull(SemanticsProperties.ContentDescription)
                    descriptions?.any { desc ->
                        desc.contains("Feature Tour") && desc.contains("Step 2 of 5")
                    } == true
                },
                useUnmergedTree = true,
            )
            .assertExists()
    }

    @Test
    fun cta_button_has_click_action() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Test",
                        description = "Description.",
                        ctaText = "Got it!",
                        currentStep = 1,
                        totalSteps = 1,
                        colors = LightCoachmarkColors,
                        onCtaClick = {},
                    )
                }
            }
        }

        composeTestRule
            .onNodeWithText("Got it!")
            .assertIsDisplayed()
            .assert(hasClickAction())
    }

    @Test
    fun skip_button_is_displayed_and_clickable() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Test",
                        description = "Description.",
                        ctaText = "Next",
                        currentStep = 1,
                        totalSteps = 3,
                        colors = LightCoachmarkColors,
                        showSkipButton = true,
                        skipButtonText = "Skip tour",
                        onCtaClick = {},
                        onSkipClick = {},
                    )
                }
            }
        }

        composeTestRule
            .onNodeWithText("Skip tour")
            .assertIsDisplayed()
            .assert(hasClickAction())
    }

    @Test
    fun progress_indicator_has_step_description() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Step",
                        description = "Multi-step tour.",
                        ctaText = "Next",
                        currentStep = 2,
                        totalSteps = 4,
                        colors = LightCoachmarkColors,
                        onCtaClick = {},
                    )
                }
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Step 2 of 4")
            .assertIsDisplayed()
    }

    @Test
    fun dont_show_again_checkbox_is_displayed() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Tip",
                        description = "A helpful tip.",
                        ctaText = "Got it!",
                        currentStep = 1,
                        totalSteps = 1,
                        colors = LightCoachmarkColors,
                        showDontShowAgain = true,
                        dontShowAgainText = "Don't show again",
                        onCtaClick = {},
                    )
                }
            }
        }

        composeTestRule
            .onNodeWithText("Don't show again")
            .assertIsDisplayed()
    }

    @Test
    fun single_step_hides_progress_indicator() {
        composeTestRule.setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                    CoachmarkTooltip(
                        title = "Solo",
                        description = "Single step coachmark.",
                        ctaText = "Got it!",
                        currentStep = 1,
                        totalSteps = 1,
                        colors = LightCoachmarkColors,
                        onCtaClick = {},
                    )
                }
            }
        }

        composeTestRule
            .onAllNodes(hasContentDescription("Step 1 of 1"))
            .fetchSemanticsNodes()
            .let { nodes ->
                assert(nodes.isEmpty()) { "Progress indicator should not be shown for single step" }
            }
    }
}
