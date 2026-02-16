package io.luminos

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class CoachmarkScrimTest {

    private fun target(
        id: String = "t1",
        title: String = "Feature",
        description: String = "Try this feature",
        targetTapBehavior: TargetTapBehavior = TargetTapBehavior.PASS_THROUGH,
    ) = CoachmarkTarget(
        id = id,
        title = title,
        description = description,
        targetTapBehavior = targetTapBehavior,
    )

    @Test
    fun buildScrimContentDescription_includes_step_info() {
        val result = buildScrimContentDescription(
            target = target(),
            currentStep = 2,
            totalSteps = 5,
            scrimTapBehavior = ScrimTapBehavior.DISMISS,
        )
        assertContains(result, "Step 2 of 5")
    }

    @Test
    fun buildScrimContentDescription_includes_target_tap_hint_for_ADVANCE() {
        val result = buildScrimContentDescription(
            target = target(targetTapBehavior = TargetTapBehavior.ADVANCE),
            currentStep = 1,
            totalSteps = 1,
            scrimTapBehavior = ScrimTapBehavior.DISMISS,
        )
        assertContains(result, "Tap the highlighted area to advance")
    }

    @Test
    fun buildScrimContentDescription_includes_dismiss_hint() {
        val result = buildScrimContentDescription(
            target = target(),
            currentStep = 1,
            totalSteps = 1,
            scrimTapBehavior = ScrimTapBehavior.DISMISS,
        )
        assertContains(result, "Tap outside to dismiss")
    }

    @Test
    fun buildScrimContentDescription_excludes_dismiss_hint_for_NONE() {
        val result = buildScrimContentDescription(
            target = target(),
            currentStep = 1,
            totalSteps = 1,
            scrimTapBehavior = ScrimTapBehavior.NONE,
        )
        assertFalse(result.contains("Tap outside"))
    }
}
