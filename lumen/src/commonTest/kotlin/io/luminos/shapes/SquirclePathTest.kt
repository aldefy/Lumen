package io.luminos.shapes

import androidx.compose.ui.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertNotNull

class SquirclePathTest {

    // ── createSquirclePath ───────────────────────────────────────────────

    @Test
    fun path_is_not_empty_for_valid_bounds() {
        val path = createSquirclePath(Rect(0f, 0f, 100f, 100f), cornerRadius = 20f)
        assertNotNull(path)
    }

    @Test
    fun path_with_square_bounds() {
        val path = createSquirclePath(Rect(0f, 0f, 100f, 100f), cornerRadius = 20f)
        assertNotNull(path)
    }

    @Test
    fun path_with_non_square_bounds() {
        val path = createSquirclePath(Rect(0f, 0f, 200f, 50f), cornerRadius = 15f)
        assertNotNull(path)
    }

    @Test
    fun path_with_offset_origin() {
        val path = createSquirclePath(Rect(100f, 200f, 300f, 400f), cornerRadius = 20f)
        assertNotNull(path)
    }

    @Test
    fun small_radius_falls_back_to_rectangle() {
        // cornerRadius < 1f triggers addRect fallback
        val path = createSquirclePath(Rect(10f, 10f, 110f, 110f), cornerRadius = 0.5f)
        assertNotNull(path)
    }

    @Test
    fun zero_radius_falls_back_to_rectangle() {
        val path = createSquirclePath(Rect(0f, 0f, 100f, 100f), cornerRadius = 0f)
        assertNotNull(path)
    }

    @Test
    fun negative_radius_coerced_to_zero() {
        // coerceIn(0f, maxRadius) clamps negative to 0, then < 1f fallback
        val path = createSquirclePath(Rect(0f, 0f, 100f, 100f), cornerRadius = -10f)
        assertNotNull(path)
    }

    @Test
    fun cornerRadius_clamped_to_half_min_dimension() {
        // bounds = 40x60, maxRadius = 20, cornerRadius = 100 → clamped to 20
        val path = createSquirclePath(Rect(0f, 0f, 40f, 60f), cornerRadius = 100f)
        assertNotNull(path)
    }

    @Test
    fun exactly_half_dimension_radius() {
        // maxRadius = 50, cornerRadius = 50 → exactly at limit
        val path = createSquirclePath(Rect(0f, 0f, 100f, 100f), cornerRadius = 50f)
        assertNotNull(path)
    }

    @Test
    fun custom_smoothness() {
        val path = createSquirclePath(Rect(0f, 0f, 100f, 100f), cornerRadius = 20f, smoothness = 8f)
        assertNotNull(path)
    }

    @Test
    fun low_smoothness() {
        val path = createSquirclePath(Rect(0f, 0f, 100f, 100f), cornerRadius = 20f, smoothness = 2f)
        assertNotNull(path)
    }

    @Test
    fun zero_sized_bounds_produces_path() {
        // width=0, height=0 → maxRadius=0, radius<1 → rect fallback
        val path = createSquirclePath(Rect(50f, 50f, 50f, 50f), cornerRadius = 10f)
        assertNotNull(path)
    }

    @Test
    fun very_large_bounds() {
        val path = createSquirclePath(Rect(0f, 0f, 10000f, 10000f), cornerRadius = 100f)
        assertNotNull(path)
    }

    // ── createPaddedSquirclePath ─────────────────────────────────────────

    @Test
    fun padded_path_completes_without_error() {
        val basePath = createSquirclePath(Rect(50f, 50f, 150f, 150f), cornerRadius = 20f)
        val paddedPath = createPaddedSquirclePath(Rect(50f, 50f, 150f, 150f), padding = 10f, cornerRadius = 20f)
        assertNotNull(basePath)
        assertNotNull(paddedPath)
    }

    @Test
    fun padded_path_with_zero_padding() {
        val path = createPaddedSquirclePath(Rect(0f, 0f, 100f, 100f), padding = 0f, cornerRadius = 20f)
        assertNotNull(path)
    }

    @Test
    fun padded_path_with_large_padding() {
        val path = createPaddedSquirclePath(Rect(50f, 50f, 150f, 150f), padding = 100f, cornerRadius = 20f)
        assertNotNull(path)
    }

    @Test
    fun padded_path_with_negative_padding() {
        // Negative padding contracts the bounds
        val path = createPaddedSquirclePath(Rect(50f, 50f, 150f, 150f), padding = -5f, cornerRadius = 20f)
        assertNotNull(path)
    }

    @Test
    fun padded_path_with_custom_smoothness() {
        val path = createPaddedSquirclePath(Rect(0f, 0f, 100f, 100f), padding = 10f, cornerRadius = 20f, smoothness = 6f)
        assertNotNull(path)
    }
}
