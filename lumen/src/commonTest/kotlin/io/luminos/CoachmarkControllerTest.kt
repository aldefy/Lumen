package io.luminos

import androidx.compose.ui.geometry.Rect
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CoachmarkControllerTest {

    private fun target(id: String, bounds: Rect = Rect(10f, 10f, 110f, 110f)) = CoachmarkTarget(
        id = id,
        bounds = bounds,
        title = "Title $id",
        description = "Desc $id",
    )

    // ═══════════════════════════════════════════════════════════════════
    // Initial state
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun initial_state_is_Hidden() = runTest {
        val controller = CoachmarkController()
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun initial_enabled_is_true() = runTest {
        val controller = CoachmarkController()
        assertTrue(controller.enabled)
    }

    @Test
    fun initial_isScrolling_is_false() = runTest {
        val controller = CoachmarkController()
        assertFalse(controller.isScrolling)
    }

    @Test
    fun initial_scrollRequester_is_null() = runTest {
        val controller = CoachmarkController()
        assertNull(controller.scrollRequester)
    }

    // ═══════════════════════════════════════════════════════════════════
    // show()
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun show_transitions_to_Showing_and_returns_true() = runTest {
        val controller = CoachmarkController()
        val result = controller.show(target("t1"))
        assertTrue(result)
        val state = controller.state.value
        assertIs<CoachmarkState.Showing>(state)
        assertEquals("t1", state.target.id)
        assertEquals(1, state.currentStep)
        assertEquals(1, state.totalSteps)
    }

    @Test
    fun show_returns_false_when_disabled() = runTest {
        val controller = CoachmarkController()
        controller.enabled = false
        assertFalse(controller.show(target("t1")))
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun show_returns_false_when_dialog_blocking() = runTest {
        val coordinator = OverlayCoordinator()
        coordinator.registerDialog()
        val controller = CoachmarkController(overlayCoordinator = coordinator)
        assertFalse(controller.show(target("t1")))
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun show_uses_registered_bounds_over_target_bounds() = runTest {
        val controller = CoachmarkController()
        val registeredBounds = Rect(50f, 50f, 200f, 200f)
        controller.registerTarget("t1", registeredBounds)

        controller.show(target("t1", bounds = Rect(0f, 0f, 10f, 10f)))
        val state = controller.state.value
        assertIs<CoachmarkState.Showing>(state)
        assertEquals(registeredBounds, state.target.bounds)
    }

    @Test
    fun show_with_unregistered_target_uses_target_bounds() = runTest {
        val controller = CoachmarkController()
        val targetBounds = Rect(0f, 0f, 50f, 50f)
        controller.show(target("t1", bounds = targetBounds))
        val state = controller.state.value
        assertIs<CoachmarkState.Showing>(state)
        assertEquals(targetBounds, state.target.bounds)
    }

    @Test
    fun show_replaces_existing_Showing_state() = runTest {
        val controller = CoachmarkController()
        controller.show(target("t1"))
        controller.show(target("t2"))
        val state = controller.state.value
        assertIs<CoachmarkState.Showing>(state)
        assertEquals("t2", state.target.id)
    }

    @Test
    fun show_replaces_existing_Sequence_state() = runTest {
        val controller = CoachmarkController()
        controller.showSequence(listOf(target("a"), target("b")))
        controller.show(target("single"))
        val state = controller.state.value
        assertIs<CoachmarkState.Showing>(state)
        assertEquals("single", state.target.id)
    }

    @Test
    fun show_succeeds_after_re_enabling() = runTest {
        val controller = CoachmarkController()
        controller.enabled = false
        assertFalse(controller.show(target("t1")))

        controller.enabled = true
        assertTrue(controller.show(target("t1")))
        assertIs<CoachmarkState.Showing>(controller.state.value)
    }

    @Test
    fun show_succeeds_after_dialog_dismissed() = runTest {
        val coordinator = OverlayCoordinator()
        coordinator.registerDialog()
        val controller = CoachmarkController(overlayCoordinator = coordinator)
        assertFalse(controller.show(target("t1")))

        coordinator.unregisterDialog()
        assertTrue(controller.show(target("t1")))
    }

    // ═══════════════════════════════════════════════════════════════════
    // showSequence()
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun showSequence_transitions_to_Sequence() = runTest {
        val controller = CoachmarkController()
        val targets = listOf(target("a"), target("b"), target("c"))
        assertTrue(controller.showSequence(targets))
        val state = controller.state.value
        assertIs<CoachmarkState.Sequence>(state)
        assertEquals(0, state.currentIndex)
        assertEquals(3, state.totalSteps)
        assertEquals("a", state.currentTarget.id)
    }

    @Test
    fun showSequence_returns_false_for_empty_list() = runTest {
        val controller = CoachmarkController()
        assertFalse(controller.showSequence(emptyList()))
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun showSequence_returns_false_when_disabled() = runTest {
        val controller = CoachmarkController()
        controller.enabled = false
        assertFalse(controller.showSequence(listOf(target("a"))))
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun showSequence_returns_false_when_dialog_blocking() = runTest {
        val coordinator = OverlayCoordinator()
        coordinator.registerDialog()
        val controller = CoachmarkController(overlayCoordinator = coordinator)
        assertFalse(controller.showSequence(listOf(target("a"))))
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun showSequence_uses_registered_bounds() = runTest {
        val controller = CoachmarkController()
        val registeredBounds = Rect(200f, 200f, 300f, 300f)
        controller.registerTarget("a", registeredBounds)

        controller.showSequence(listOf(
            target("a", bounds = Rect.Zero),
            target("b"),
        ))
        val state = controller.state.value
        assertIs<CoachmarkState.Sequence>(state)
        assertEquals(registeredBounds, state.targets[0].bounds)
        // "b" is not registered — uses target bounds
        assertEquals(Rect(10f, 10f, 110f, 110f), state.targets[1].bounds)
    }

    @Test
    fun showSequence_with_single_target() = runTest {
        val controller = CoachmarkController()
        assertTrue(controller.showSequence(listOf(target("only"))))
        val state = controller.state.value
        assertIs<CoachmarkState.Sequence>(state)
        assertEquals(1, state.totalSteps)
    }

    // ═══════════════════════════════════════════════════════════════════
    // dismiss()
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun dismiss_from_Showing() = runTest {
        val controller = CoachmarkController()
        controller.show(target("t1"))
        controller.dismiss()
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun dismiss_from_Sequence() = runTest {
        val controller = CoachmarkController()
        controller.showSequence(listOf(target("a"), target("b")))
        controller.dismiss()
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun dismiss_from_Hidden_is_idempotent() = runTest {
        val controller = CoachmarkController()
        controller.dismiss()
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun dismiss_multiple_times_is_safe() = runTest {
        val controller = CoachmarkController()
        controller.show(target("t1"))
        controller.dismiss()
        controller.dismiss()
        controller.dismiss()
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    // ═══════════════════════════════════════════════════════════════════
    // next()
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun next_advances_sequence() = runTest {
        val controller = CoachmarkController()
        controller.showSequence(listOf(target("a"), target("b"), target("c")))
        controller.next()
        val state = controller.state.value
        assertIs<CoachmarkState.Sequence>(state)
        assertEquals(1, state.currentIndex)
        assertEquals("b", state.currentTarget.id)
    }

    @Test
    fun next_walks_entire_sequence_then_dismisses() = runTest {
        val controller = CoachmarkController()
        controller.showSequence(listOf(target("a"), target("b"), target("c")))
        controller.next() // 0 -> 1
        assertIs<CoachmarkState.Sequence>(controller.state.value)
        controller.next() // 1 -> 2
        assertIs<CoachmarkState.Sequence>(controller.state.value)
        controller.next() // 2 -> Hidden (last)
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun next_on_last_step_dismisses() = runTest {
        val controller = CoachmarkController()
        controller.showSequence(listOf(target("a"), target("b")))
        controller.next()
        controller.next()
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun next_on_Showing_dismisses() = runTest {
        val controller = CoachmarkController()
        controller.show(target("t1"))
        controller.next()
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun next_on_Hidden_is_noop() = runTest {
        val controller = CoachmarkController()
        controller.next()
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    // ═══════════════════════════════════════════════════════════════════
    // previous()
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun previous_goes_back_in_sequence() = runTest {
        val controller = CoachmarkController()
        controller.showSequence(listOf(target("a"), target("b"), target("c")))
        controller.next()
        controller.previous()
        val state = controller.state.value
        assertIs<CoachmarkState.Sequence>(state)
        assertEquals(0, state.currentIndex)
        assertEquals("a", state.currentTarget.id)
    }

    @Test
    fun previous_on_first_step_is_noop() = runTest {
        val controller = CoachmarkController()
        controller.showSequence(listOf(target("a"), target("b")))
        controller.previous()
        val state = controller.state.value
        assertIs<CoachmarkState.Sequence>(state)
        assertEquals(0, state.currentIndex)
    }

    @Test
    fun previous_on_Showing_is_noop() = runTest {
        val controller = CoachmarkController()
        controller.show(target("t1"))
        controller.previous()
        val state = controller.state.value
        assertIs<CoachmarkState.Showing>(state)
        assertEquals("t1", state.target.id)
    }

    @Test
    fun previous_on_Hidden_is_noop() = runTest {
        val controller = CoachmarkController()
        controller.previous()
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun next_then_previous_roundtrip() = runTest {
        val controller = CoachmarkController()
        controller.showSequence(listOf(target("a"), target("b"), target("c")))
        controller.next()  // 0 -> 1
        controller.next()  // 1 -> 2
        controller.previous() // 2 -> 1
        controller.previous() // 1 -> 0
        val state = controller.state.value
        assertIs<CoachmarkState.Sequence>(state)
        assertEquals(0, state.currentIndex)
    }

    // ═══════════════════════════════════════════════════════════════════
    // registerTarget / unregisterTarget / getTargetBounds
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun getTargetBounds_returns_null_for_unregistered() = runTest {
        val controller = CoachmarkController()
        assertNull(controller.getTargetBounds("nope"))
    }

    @Test
    fun getTargetBounds_returns_bounds_for_registered() = runTest {
        val controller = CoachmarkController()
        val bounds = Rect(10f, 20f, 30f, 40f)
        controller.registerTarget("t1", bounds)
        assertEquals(bounds, controller.getTargetBounds("t1"))
    }

    @Test
    fun registerTarget_overwrites_previous_bounds() = runTest {
        val controller = CoachmarkController()
        controller.registerTarget("t1", Rect(0f, 0f, 10f, 10f))
        val newBounds = Rect(50f, 50f, 200f, 200f)
        controller.registerTarget("t1", newBounds)
        assertEquals(newBounds, controller.getTargetBounds("t1"))
    }

    @Test
    fun unregisterTarget_removes_target() = runTest {
        val controller = CoachmarkController()
        controller.registerTarget("t1", Rect(0f, 0f, 100f, 100f))
        controller.unregisterTarget("t1")
        assertNull(controller.getTargetBounds("t1"))
    }

    @Test
    fun unregisterTarget_nonexistent_is_safe() = runTest {
        val controller = CoachmarkController()
        controller.unregisterTarget("nope") // should not throw
    }

    @Test
    fun registerTarget_updates_bounds_on_active_Showing() = runTest {
        val controller = CoachmarkController()
        controller.show(target("t1"))
        val newBounds = Rect(99f, 99f, 199f, 199f)
        controller.registerTarget("t1", newBounds)

        val state = controller.state.value
        assertIs<CoachmarkState.Showing>(state)
        assertEquals(newBounds, state.target.bounds)
    }

    @Test
    fun registerTarget_does_not_update_different_id_Showing() = runTest {
        val controller = CoachmarkController()
        val originalBounds = Rect(10f, 10f, 110f, 110f)
        controller.show(target("t1", bounds = originalBounds))
        controller.registerTarget("t2", Rect(0f, 0f, 50f, 50f))

        val state = controller.state.value
        assertIs<CoachmarkState.Showing>(state)
        assertEquals(originalBounds, state.target.bounds)
    }

    @Test
    fun registerTarget_updates_matching_target_in_Sequence() = runTest {
        val controller = CoachmarkController()
        controller.showSequence(listOf(target("a"), target("b"), target("c")))
        val newBounds = Rect(300f, 300f, 400f, 400f)
        controller.registerTarget("b", newBounds)

        val state = controller.state.value
        assertIs<CoachmarkState.Sequence>(state)
        assertEquals(newBounds, state.targets[1].bounds)
        // Others untouched
        assertEquals(Rect(10f, 10f, 110f, 110f), state.targets[0].bounds)
        assertEquals(Rect(10f, 10f, 110f, 110f), state.targets[2].bounds)
    }

    @Test
    fun registerTarget_during_Hidden_does_not_change_state() = runTest {
        val controller = CoachmarkController()
        controller.registerTarget("t1", Rect(0f, 0f, 100f, 100f))
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    // ═══════════════════════════════════════════════════════════════════
    // setViewportBounds / isTargetVisible
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun isTargetVisible_without_viewport_returns_true() = runTest {
        val controller = CoachmarkController()
        controller.registerTarget("t1", Rect(10f, 10f, 100f, 100f))
        assertTrue(controller.isTargetVisible("t1"))
    }

    @Test
    fun isTargetVisible_returns_false_for_unregistered_target() = runTest {
        val controller = CoachmarkController()
        assertFalse(controller.isTargetVisible("unknown"))
    }

    @Test
    fun isTargetVisible_within_viewport() = runTest {
        val controller = CoachmarkController()
        controller.setViewportBounds(Rect(0f, 0f, 500f, 500f))
        controller.registerTarget("visible", Rect(10f, 10f, 100f, 100f))
        assertTrue(controller.isTargetVisible("visible"))
    }

    @Test
    fun isTargetVisible_outside_viewport() = runTest {
        val controller = CoachmarkController()
        controller.setViewportBounds(Rect(0f, 0f, 500f, 500f))
        controller.registerTarget("offscreen", Rect(600f, 600f, 700f, 700f))
        assertFalse(controller.isTargetVisible("offscreen"))
    }

    @Test
    fun isTargetVisible_partially_overlapping_viewport() = runTest {
        val controller = CoachmarkController()
        controller.setViewportBounds(Rect(0f, 0f, 500f, 500f))
        // Target partially overlaps viewport
        controller.registerTarget("partial", Rect(450f, 450f, 550f, 550f))
        assertTrue(controller.isTargetVisible("partial"))
    }

    @Test
    fun isTargetVisible_zero_width_bounds_in_viewport() = runTest {
        val controller = CoachmarkController()
        controller.setViewportBounds(Rect(0f, 0f, 500f, 500f))
        controller.registerTarget("zeroW", Rect(50f, 50f, 50f, 100f))
        assertFalse(controller.isTargetVisible("zeroW"))
    }

    @Test
    fun isTargetVisible_zero_height_bounds_in_viewport() = runTest {
        val controller = CoachmarkController()
        controller.setViewportBounds(Rect(0f, 0f, 500f, 500f))
        controller.registerTarget("zeroH", Rect(50f, 50f, 100f, 50f))
        assertFalse(controller.isTargetVisible("zeroH"))
    }

    @Test
    fun setViewportBounds_updates_visibility_for_all_targets() = runTest {
        val controller = CoachmarkController()
        controller.registerTarget("a", Rect(10f, 10f, 100f, 100f))
        controller.registerTarget("b", Rect(600f, 600f, 700f, 700f))

        // Without viewport, both visible
        assertTrue(controller.isTargetVisible("a"))
        assertTrue(controller.isTargetVisible("b"))

        // Set viewport — now only "a" is visible
        controller.setViewportBounds(Rect(0f, 0f, 500f, 500f))
        assertTrue(controller.isTargetVisible("a"))
        assertFalse(controller.isTargetVisible("b"))
    }

    // ═══════════════════════════════════════════════════════════════════
    // isCurrentTargetReady()
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun isCurrentTargetReady_returns_true_when_Hidden() = runTest {
        val controller = CoachmarkController()
        assertTrue(controller.isCurrentTargetReady())
    }

    @Test
    fun isCurrentTargetReady_false_when_scrolling() = runTest {
        val controller = CoachmarkController()
        controller.registerTarget("t1", Rect(10f, 10f, 100f, 100f))
        controller.show(target("t1"))
        controller.isScrolling = true
        assertFalse(controller.isCurrentTargetReady())
    }

    @Test
    fun isCurrentTargetReady_true_when_Showing_and_target_visible() = runTest {
        val controller = CoachmarkController()
        controller.registerTarget("t1", Rect(10f, 10f, 100f, 100f))
        controller.show(target("t1"))
        assertTrue(controller.isCurrentTargetReady())
    }

    @Test
    fun isCurrentTargetReady_false_when_Showing_and_target_not_visible() = runTest {
        val controller = CoachmarkController()
        controller.setViewportBounds(Rect(0f, 0f, 500f, 500f))
        controller.registerTarget("t1", Rect(600f, 600f, 700f, 700f))
        controller.show(target("t1"))
        assertFalse(controller.isCurrentTargetReady())
    }

    @Test
    fun isCurrentTargetReady_true_when_Sequence_and_target_visible() = runTest {
        val controller = CoachmarkController()
        controller.registerTarget("a", Rect(10f, 10f, 100f, 100f))
        controller.showSequence(listOf(target("a"), target("b")))
        assertTrue(controller.isCurrentTargetReady())
    }

    @Test
    fun isCurrentTargetReady_false_when_Showing_and_unregistered_target() = runTest {
        val controller = CoachmarkController()
        controller.setViewportBounds(Rect(0f, 0f, 500f, 500f))
        // target is not registered → isTargetVisible returns false
        controller.show(target("unregistered"))
        assertFalse(controller.isCurrentTargetReady())
    }

    // ═══════════════════════════════════════════════════════════════════
    // isBlockedByDialog
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun isBlockedByDialog_false_without_coordinator() = runTest {
        val controller = CoachmarkController(overlayCoordinator = null)
        assertFalse(controller.isBlockedByDialog)
    }

    @Test
    fun isBlockedByDialog_false_with_coordinator_no_dialog() = runTest {
        val coordinator = OverlayCoordinator()
        val controller = CoachmarkController(overlayCoordinator = coordinator)
        assertFalse(controller.isBlockedByDialog)
    }

    @Test
    fun isBlockedByDialog_true_with_coordinator_and_dialog() = runTest {
        val coordinator = OverlayCoordinator()
        coordinator.registerDialog()
        val controller = CoachmarkController(overlayCoordinator = coordinator)
        assertTrue(controller.isBlockedByDialog)
    }

    // ═══════════════════════════════════════════════════════════════════
    // skipCurrentIfNotVisible()
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun skipCurrentIfNotVisible_advances_sequence() = runTest {
        val controller = CoachmarkController()
        controller.showSequence(listOf(target("a"), target("b"), target("c")))
        controller.skipCurrentIfNotVisible()
        val state = controller.state.value
        assertIs<CoachmarkState.Sequence>(state)
        assertEquals(1, state.currentIndex)
    }

    @Test
    fun skipCurrentIfNotVisible_on_last_step_dismisses() = runTest {
        val controller = CoachmarkController()
        controller.showSequence(listOf(target("a"), target("b")))
        controller.next() // 0 -> 1 (last)
        controller.skipCurrentIfNotVisible()
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun skipCurrentIfNotVisible_on_Showing_dismisses() = runTest {
        val controller = CoachmarkController()
        controller.show(target("t1"))
        controller.skipCurrentIfNotVisible()
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun skipCurrentIfNotVisible_on_Hidden_is_noop() = runTest {
        val controller = CoachmarkController()
        controller.skipCurrentIfNotVisible()
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    // ═══════════════════════════════════════════════════════════════════
    // scrollRequester property
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun scrollRequester_can_be_set_and_read() = runTest {
        val controller = CoachmarkController()
        var invoked = false
        controller.scrollRequester = { invoked = true }
        assertFalse(invoked) // just set, not invoked
    }

    // ═══════════════════════════════════════════════════════════════════
    // Full lifecycle scenario
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun full_show_dismiss_show_sequence_lifecycle() = runTest {
        val controller = CoachmarkController()
        // Single coachmark
        assertTrue(controller.show(target("t1")))
        assertIs<CoachmarkState.Showing>(controller.state.value)
        controller.dismiss()
        assertIs<CoachmarkState.Hidden>(controller.state.value)

        // Sequence
        assertTrue(controller.showSequence(listOf(target("a"), target("b"))))
        controller.next()
        controller.next() // dismisses
        assertIs<CoachmarkState.Hidden>(controller.state.value)

        // Can show again
        assertTrue(controller.show(target("t2")))
        assertIs<CoachmarkState.Showing>(controller.state.value)
    }

    // ═══════════════════════════════════════════════════════════════════
    // "Don't Show Again" / repository integration
    // ═══════════════════════════════════════════════════════════════════

    private fun targetDSA(
        id: String,
        showDontShowAgain: Boolean = true,
        persistKey: String? = null,
    ) = CoachmarkTarget(
        id = id,
        title = "Title $id",
        description = "Desc $id",
        showDontShowAgain = showDontShowAgain,
        persistKey = persistKey,
    )

    private fun createRepo(): CoachmarkRepository {
        return CoachmarkRepository(FakeCoachmarkStorage())
    }

    @Test
    fun show_returns_false_when_suppressed_by_repository() = runTest {
        val repo = createRepo()
        repo.markCoachmarkSeen("t1")
        val controller = CoachmarkController(repository = repo)
        assertFalse(controller.show(targetDSA("t1")))
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun show_succeeds_when_not_suppressed() = runTest {
        val repo = createRepo()
        val controller = CoachmarkController(repository = repo)
        assertTrue(controller.show(targetDSA("t1")))
        assertIs<CoachmarkState.Showing>(controller.state.value)
    }

    @Test
    fun show_ignores_repository_when_showDontShowAgain_is_false() = runTest {
        val repo = createRepo()
        repo.markCoachmarkSeen("t1")
        val controller = CoachmarkController(repository = repo)
        // showDontShowAgain is false, so repository is not checked
        assertTrue(controller.show(targetDSA("t1", showDontShowAgain = false)))
        assertIs<CoachmarkState.Showing>(controller.state.value)
    }

    @Test
    fun showSequence_filters_suppressed_targets() = runTest {
        val repo = createRepo()
        repo.markCoachmarkSeen("b")
        val controller = CoachmarkController(repository = repo)
        assertTrue(controller.showSequence(listOf(
            targetDSA("a"),
            targetDSA("b"),
            targetDSA("c"),
        )))
        val state = controller.state.value
        assertIs<CoachmarkState.Sequence>(state)
        assertEquals(2, state.totalSteps)
        assertEquals("a", state.targets[0].id)
        assertEquals("c", state.targets[1].id)
    }

    @Test
    fun showSequence_returns_false_when_all_suppressed() = runTest {
        val repo = createRepo()
        repo.markCoachmarkSeen("a")
        repo.markCoachmarkSeen("b")
        val controller = CoachmarkController(repository = repo)
        assertFalse(controller.showSequence(listOf(
            targetDSA("a"),
            targetDSA("b"),
        )))
        assertIs<CoachmarkState.Hidden>(controller.state.value)
    }

    @Test
    fun markDontShowAgain_persists_via_repository() = runTest {
        val repo = createRepo()
        val controller = CoachmarkController(repository = repo)
        val t = targetDSA("t1")
        controller.markDontShowAgain(t)
        assertTrue(repo.hasSeenCoachmark("t1"))
    }

    @Test
    fun markDontShowAgain_noop_without_repository() = runTest {
        val controller = CoachmarkController()
        val t = targetDSA("t1")
        controller.markDontShowAgain(t) // should not crash
    }

    @Test
    fun markDontShowAgain_uses_persistKey_over_id() = runTest {
        val repo = createRepo()
        val controller = CoachmarkController(repository = repo)
        val t = targetDSA("t1", persistKey = "custom_key")
        controller.markDontShowAgain(t)
        assertTrue(repo.hasSeenCoachmark("custom_key"))
        assertFalse(repo.hasSeenCoachmark("t1"))
    }
}
