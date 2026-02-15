package io.luminos

import androidx.compose.ui.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class CoachmarkStateTest {

    private fun target(id: String) = CoachmarkTarget(
        id = id,
        bounds = Rect(0f, 0f, 100f, 100f),
        title = "Title $id",
        description = "Desc $id",
    )

    // ── Hidden ───────────────────────────────────────────────────────────

    @Test
    fun hidden_is_singleton() {
        assertSame(CoachmarkState.Hidden, CoachmarkState.Hidden)
    }

    @Test
    fun hidden_is_CoachmarkState() {
        assertIs<CoachmarkState>(CoachmarkState.Hidden)
    }

    // ── Showing ──────────────────────────────────────────────────────────

    @Test
    fun showing_defaults() {
        val state = CoachmarkState.Showing(target = target("t1"))
        assertEquals(1, state.currentStep)
        assertEquals(1, state.totalSteps)
    }

    @Test
    fun showing_with_custom_step_values() {
        val state = CoachmarkState.Showing(
            target = target("t1"),
            currentStep = 3,
            totalSteps = 5,
        )
        assertEquals(3, state.currentStep)
        assertEquals(5, state.totalSteps)
    }

    @Test
    fun showing_preserves_target() {
        val t = target("t1")
        val state = CoachmarkState.Showing(target = t)
        assertEquals(t, state.target)
        assertEquals("t1", state.target.id)
    }

    @Test
    fun showing_data_class_equality() {
        val t = target("t1")
        val a = CoachmarkState.Showing(target = t, currentStep = 1, totalSteps = 1)
        val b = CoachmarkState.Showing(target = t, currentStep = 1, totalSteps = 1)
        assertEquals(a, b)
    }

    @Test
    fun showing_data_class_inequality_on_step() {
        val t = target("t1")
        val a = CoachmarkState.Showing(target = t, currentStep = 1, totalSteps = 3)
        val b = CoachmarkState.Showing(target = t, currentStep = 2, totalSteps = 3)
        assertNotEquals(a, b)
    }

    @Test
    fun showing_copy_updates_target() {
        val state = CoachmarkState.Showing(target = target("t1"))
        val updated = state.copy(target = target("t2"))
        assertEquals("t2", updated.target.id)
        assertEquals("t1", state.target.id)
    }

    // ── Sequence — currentTarget ─────────────────────────────────────────

    @Test
    fun sequence_currentTarget_at_index_0() {
        val targets = listOf(target("a"), target("b"), target("c"))
        val state = CoachmarkState.Sequence(targets = targets, currentIndex = 0)
        assertEquals("a", state.currentTarget.id)
    }

    @Test
    fun sequence_currentTarget_at_middle() {
        val targets = listOf(target("a"), target("b"), target("c"))
        val state = CoachmarkState.Sequence(targets = targets, currentIndex = 1)
        assertEquals("b", state.currentTarget.id)
    }

    @Test
    fun sequence_currentTarget_at_last() {
        val targets = listOf(target("a"), target("b"), target("c"))
        val state = CoachmarkState.Sequence(targets = targets, currentIndex = 2)
        assertEquals("c", state.currentTarget.id)
    }

    // ── Sequence — currentStep (1-indexed) ───────────────────────────────

    @Test
    fun sequence_currentStep_is_1_indexed() {
        val targets = listOf(target("a"), target("b"), target("c"))
        assertEquals(1, CoachmarkState.Sequence(targets, 0).currentStep)
        assertEquals(2, CoachmarkState.Sequence(targets, 1).currentStep)
        assertEquals(3, CoachmarkState.Sequence(targets, 2).currentStep)
    }

    // ── Sequence — totalSteps ────────────────────────────────────────────

    @Test
    fun sequence_totalSteps_matches_targets_size() {
        val s1 = CoachmarkState.Sequence(listOf(target("a")), 0)
        assertEquals(1, s1.totalSteps)

        val s3 = CoachmarkState.Sequence(listOf(target("a"), target("b"), target("c")), 0)
        assertEquals(3, s3.totalSteps)
    }

    // ── Sequence — hasNext / hasPrevious ──────────────────────────────────

    @Test
    fun sequence_hasNext_and_hasPrevious_at_first() {
        val targets = listOf(target("a"), target("b"), target("c"))
        val state = CoachmarkState.Sequence(targets = targets, currentIndex = 0)
        assertTrue(state.hasNext)
        assertFalse(state.hasPrevious)
    }

    @Test
    fun sequence_hasNext_and_hasPrevious_at_middle() {
        val targets = listOf(target("a"), target("b"), target("c"))
        val state = CoachmarkState.Sequence(targets = targets, currentIndex = 1)
        assertTrue(state.hasNext)
        assertTrue(state.hasPrevious)
    }

    @Test
    fun sequence_hasNext_and_hasPrevious_at_last() {
        val targets = listOf(target("a"), target("b"), target("c"))
        val state = CoachmarkState.Sequence(targets = targets, currentIndex = 2)
        assertFalse(state.hasNext)
        assertTrue(state.hasPrevious)
    }

    @Test
    fun sequence_with_single_target() {
        val state = CoachmarkState.Sequence(listOf(target("only")), 0)
        assertFalse(state.hasNext)
        assertFalse(state.hasPrevious)
        assertEquals(1, state.totalSteps)
        assertEquals(1, state.currentStep)
    }

    @Test
    fun sequence_with_two_targets_at_first() {
        val targets = listOf(target("a"), target("b"))
        val state = CoachmarkState.Sequence(targets, 0)
        assertTrue(state.hasNext)
        assertFalse(state.hasPrevious)
    }

    @Test
    fun sequence_with_two_targets_at_last() {
        val targets = listOf(target("a"), target("b"))
        val state = CoachmarkState.Sequence(targets, 1)
        assertFalse(state.hasNext)
        assertTrue(state.hasPrevious)
    }

    // ── Sequence — data class ────────────────────────────────────────────

    @Test
    fun sequence_data_class_equality() {
        val targets = listOf(target("a"), target("b"))
        val a = CoachmarkState.Sequence(targets, 0)
        val b = CoachmarkState.Sequence(targets, 0)
        assertEquals(a, b)
    }

    @Test
    fun sequence_copy_advances_index() {
        val targets = listOf(target("a"), target("b"), target("c"))
        val state = CoachmarkState.Sequence(targets, 0)
        val advanced = state.copy(currentIndex = 1)
        assertEquals(0, state.currentIndex)
        assertEquals(1, advanced.currentIndex)
    }
}
