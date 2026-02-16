package io.luminos

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CoachmarkAnalyticsTest {

    @Test
    fun dismissReason_has_6_entries() {
        val values = DismissReason.entries
        assertEquals(6, values.size)
    }

    @Test
    fun analytics_defaults_are_noops() {
        val analytics = CoachmarkAnalytics()
        // Calling each default lambda should not crash
        analytics.onShow("id", 0, 1)
        analytics.onDismiss("id", 0, 1, DismissReason.SCRIM_TAP)
        analytics.onAdvance("from", "to", 0, 2)
        analytics.onComplete(3)
    }

    @Test
    fun analytics_data_class_equality() {
        val a = CoachmarkAnalytics()
        val b = CoachmarkAnalytics()
        assertEquals(a, b)
    }

    @Test
    fun analytics_copy_replaces_callback() {
        val original = CoachmarkAnalytics()
        var called = false
        val modified = original.copy(onComplete = { called = true })
        modified.onComplete(1)
        assertEquals(true, called)
        assertNotEquals(original, modified)
    }
}
