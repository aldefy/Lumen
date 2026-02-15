package io.luminos

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class OverlayCoordinatorTest {

    // ── Initial state ────────────────────────────────────────────────────

    @Test
    fun initially_no_dialog_showing() {
        val coordinator = OverlayCoordinator()
        assertFalse(coordinator.isDialogShowing)
        assertEquals(0, coordinator.activeDialogCount.value)
    }

    // ── registerDialog ───────────────────────────────────────────────────

    @Test
    fun registerDialog_increments_count() {
        val coordinator = OverlayCoordinator()
        coordinator.registerDialog()
        assertEquals(1, coordinator.activeDialogCount.value)
        assertTrue(coordinator.isDialogShowing)
    }

    @Test
    fun multiple_registerDialog_increments() {
        val coordinator = OverlayCoordinator()
        coordinator.registerDialog()
        coordinator.registerDialog()
        coordinator.registerDialog()
        assertEquals(3, coordinator.activeDialogCount.value)
    }

    @Test
    fun registerDialog_returns_unique_tokens() {
        val coordinator = OverlayCoordinator()
        val token1 = coordinator.registerDialog()
        val token2 = coordinator.registerDialog()
        val token3 = coordinator.registerDialog()
        assertNotEquals(token1, token2)
        assertNotEquals(token2, token3)
        assertNotEquals(token1, token3)
    }

    @Test
    fun registerDialog_returns_monotonically_increasing_tokens() {
        val coordinator = OverlayCoordinator()
        val token1 = coordinator.registerDialog()
        val token2 = coordinator.registerDialog()
        val token3 = coordinator.registerDialog()
        assertTrue(token2 > token1)
        assertTrue(token3 > token2)
    }

    // ── unregisterDialog ─────────────────────────────────────────────────

    @Test
    fun unregisterDialog_decrements_count() {
        val coordinator = OverlayCoordinator()
        coordinator.registerDialog()
        coordinator.registerDialog()
        coordinator.unregisterDialog()
        assertEquals(1, coordinator.activeDialogCount.value)
    }

    @Test
    fun unregisterDialog_to_zero() {
        val coordinator = OverlayCoordinator()
        coordinator.registerDialog()
        coordinator.unregisterDialog()
        assertEquals(0, coordinator.activeDialogCount.value)
        assertFalse(coordinator.isDialogShowing)
    }

    @Test
    fun unregisterDialog_floors_at_zero() {
        val coordinator = OverlayCoordinator()
        coordinator.unregisterDialog()
        coordinator.unregisterDialog()
        coordinator.unregisterDialog()
        assertEquals(0, coordinator.activeDialogCount.value)
    }

    // ── isDialogShowing ──────────────────────────────────────────────────

    @Test
    fun isDialogShowing_reflects_count_lifecycle() {
        val coordinator = OverlayCoordinator()
        assertFalse(coordinator.isDialogShowing)

        coordinator.registerDialog()
        assertTrue(coordinator.isDialogShowing)

        coordinator.registerDialog()
        assertTrue(coordinator.isDialogShowing)

        coordinator.unregisterDialog()
        assertTrue(coordinator.isDialogShowing) // still 1

        coordinator.unregisterDialog()
        assertFalse(coordinator.isDialogShowing) // back to 0
    }

    // ── interleaved register/unregister ──────────────────────────────────

    @Test
    fun interleaved_register_unregister() {
        val coordinator = OverlayCoordinator()
        coordinator.registerDialog()   // 1
        coordinator.registerDialog()   // 2
        coordinator.unregisterDialog() // 1
        coordinator.registerDialog()   // 2
        coordinator.unregisterDialog() // 1
        coordinator.unregisterDialog() // 0
        assertEquals(0, coordinator.activeDialogCount.value)
        assertFalse(coordinator.isDialogShowing)
    }

    @Test
    fun tokens_keep_increasing_across_unregister_calls() {
        val coordinator = OverlayCoordinator()
        val t1 = coordinator.registerDialog()
        coordinator.unregisterDialog()
        val t2 = coordinator.registerDialog()
        assertTrue(t2 > t1)
    }
}
