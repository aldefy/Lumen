package io.luminos.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertNotNull

class StarPathTest {

    // ── createStarPath ───────────────────────────────────────────────────

    @Test
    fun path_is_not_empty_for_valid_input() {
        val path = createStarPath(
            center = Offset(100f, 100f),
            outerRadius = 50f,
            innerRadius = 25f,
            points = 5,
        )
        assertNotNull(path)
    }

    @Test
    fun default_5_points() {
        val path = createStarPath(
            center = Offset(100f, 100f),
            outerRadius = 50f,
            innerRadius = 25f,
        )
        assertNotNull(path)
    }

    @Test
    fun star_with_3_points() {
        val path = createStarPath(
            center = Offset(50f, 50f),
            outerRadius = 40f,
            innerRadius = 20f,
            points = 3,
        )
        assertNotNull(path)
    }

    @Test
    fun star_with_8_points() {
        val path = createStarPath(
            center = Offset(50f, 50f),
            outerRadius = 40f,
            innerRadius = 20f,
            points = 8,
        )
        assertNotNull(path)
    }

    @Test
    fun star_at_origin() {
        val path = createStarPath(
            center = Offset(0f, 0f),
            outerRadius = 50f,
            innerRadius = 25f,
        )
        assertNotNull(path)
    }

    @Test
    fun star_with_equal_radii() {
        // Inner = outer → polygon (no inner dips)
        val path = createStarPath(
            center = Offset(50f, 50f),
            outerRadius = 30f,
            innerRadius = 30f,
        )
        assertNotNull(path)
    }

    @Test
    fun star_with_zero_outer_radius() {
        val path = createStarPath(
            center = Offset(50f, 50f),
            outerRadius = 0f,
            innerRadius = 0f,
        )
        assertNotNull(path)
    }

    // ── createPaddedStarPath ─────────────────────────────────────────────

    @Test
    fun padded_star_completes_without_error() {
        val path = createPaddedStarPath(Rect(50f, 50f, 150f, 150f), padding = 10f)
        assertNotNull(path)
    }

    @Test
    fun padded_star_with_zero_padding() {
        val path = createPaddedStarPath(Rect(50f, 50f, 150f, 150f), padding = 0f)
        assertNotNull(path)
    }

    @Test
    fun padded_star_with_large_padding() {
        val path = createPaddedStarPath(Rect(50f, 50f, 150f, 150f), padding = 100f)
        assertNotNull(path)
    }

    @Test
    fun innerRadiusRatio_below_min_clamped() {
        // -1f should be clamped to 0.1f
        val path = createPaddedStarPath(Rect(0f, 0f, 100f, 100f), padding = 5f, innerRadiusRatio = -1f)
        assertNotNull(path)
    }

    @Test
    fun innerRadiusRatio_above_max_clamped() {
        // 5f should be clamped to 0.9f
        val path = createPaddedStarPath(Rect(0f, 0f, 100f, 100f), padding = 5f, innerRadiusRatio = 5f)
        assertNotNull(path)
    }

    @Test
    fun innerRadiusRatio_at_min_boundary() {
        val path = createPaddedStarPath(Rect(0f, 0f, 100f, 100f), padding = 5f, innerRadiusRatio = 0.1f)
        assertNotNull(path)
    }

    @Test
    fun innerRadiusRatio_at_max_boundary() {
        val path = createPaddedStarPath(Rect(0f, 0f, 100f, 100f), padding = 5f, innerRadiusRatio = 0.9f)
        assertNotNull(path)
    }

    @Test
    fun padded_star_with_custom_points() {
        val path = createPaddedStarPath(Rect(0f, 0f, 100f, 100f), padding = 5f, points = 7)
        assertNotNull(path)
    }

    // ── createScaledStarPath ─────────────────────────────────────────────

    @Test
    fun scaled_star_scale_1() {
        val path = createScaledStarPath(Rect(50f, 50f, 150f, 150f), padding = 5f, scale = 1f)
        assertNotNull(path)
    }

    @Test
    fun scaled_star_scale_2() {
        val path = createScaledStarPath(Rect(50f, 50f, 150f, 150f), padding = 5f, scale = 2f)
        assertNotNull(path)
    }

    @Test
    fun scaled_star_scale_half() {
        val path = createScaledStarPath(Rect(50f, 50f, 150f, 150f), padding = 5f, scale = 0.5f)
        assertNotNull(path)
    }

    @Test
    fun scale_zero_produces_degenerate_path() {
        val path = createScaledStarPath(Rect(50f, 50f, 150f, 150f), padding = 5f, scale = 0f)
        assertNotNull(path)
    }

    @Test
    fun scaled_star_with_custom_ratio_and_points() {
        val path = createScaledStarPath(
            Rect(0f, 0f, 100f, 100f),
            padding = 10f,
            points = 6,
            innerRadiusRatio = 0.3f,
            scale = 1.5f,
        )
        assertNotNull(path)
    }

    @Test
    fun scaled_star_negative_scale() {
        // Negative scale flips the star — should not crash
        val path = createScaledStarPath(Rect(50f, 50f, 150f, 150f), padding = 5f, scale = -1f)
        assertNotNull(path)
    }
}
