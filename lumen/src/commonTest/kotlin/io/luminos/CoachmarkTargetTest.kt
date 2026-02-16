package io.luminos

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CoachmarkTargetTest {

    // ── Default values ───────────────────────────────────────────────────

    @Test
    fun default_bounds_is_Rect_Zero() {
        val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
        assertEquals(Rect.Zero, target.bounds)
    }

    @Test
    fun default_shape_is_Circle() {
        val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
        assertIs<CutoutShape.Circle>(target.shape)
    }

    @Test
    fun default_tooltipPosition_is_AUTO() {
        val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
        assertEquals(TooltipPosition.AUTO, target.tooltipPosition)
    }

    @Test
    fun default_connectorStyle_is_AUTO() {
        val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
        assertEquals(ConnectorStyle.AUTO, target.connectorStyle)
    }

    @Test
    fun default_connectorLength_is_Unspecified() {
        val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
        assertEquals(Dp.Unspecified, target.connectorLength)
    }

    @Test
    fun default_ctaText() {
        val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
        assertEquals("Got it!", target.ctaText)
    }

    @Test
    fun default_showProgressIndicator_is_null() {
        val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
        assertNull(target.showProgressIndicator)
    }

    @Test
    fun default_highlightAnimation_is_null() {
        val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
        assertNull(target.highlightAnimation)
    }

    // ── Copy / equality ──────────────────────────────────────────────────

    @Test
    fun copy_with_new_bounds() {
        val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
        val newBounds = Rect(10f, 20f, 30f, 40f)
        val copied = target.copy(bounds = newBounds)
        assertEquals(newBounds, copied.bounds)
        assertEquals("t1", copied.id)
    }

    @Test
    fun equality_same_values() {
        val a = CoachmarkTarget(id = "t1", title = "T", description = "D")
        val b = CoachmarkTarget(id = "t1", title = "T", description = "D")
        assertEquals(a, b)
    }

    @Test
    fun inequality_different_id() {
        val a = CoachmarkTarget(id = "t1", title = "T", description = "D")
        val b = CoachmarkTarget(id = "t2", title = "T", description = "D")
        assertNotEquals(a, b)
    }

    // ── CutoutShape defaults ─────────────────────────────────────────────

    @Test
    fun circle_defaults() {
        val shape = CutoutShape.Circle()
        assertEquals(Dp.Unspecified, shape.radius)
        assertEquals(8.dp, shape.radiusPadding)
    }

    @Test
    fun roundedRect_defaults() {
        val shape = CutoutShape.RoundedRect()
        assertEquals(12.dp, shape.cornerRadius)
        assertEquals(8.dp, shape.padding)
    }

    @Test
    fun rect_defaults() {
        val shape = CutoutShape.Rect()
        assertEquals(8.dp, shape.padding)
    }

    @Test
    fun squircle_defaults() {
        val shape = CutoutShape.Squircle()
        assertEquals(20.dp, shape.cornerRadius)
        assertEquals(8.dp, shape.padding)
    }

    @Test
    fun star_defaults() {
        val shape = CutoutShape.Star()
        assertEquals(5, shape.points)
        assertEquals(0.5f, shape.innerRadiusRatio)
        assertEquals(8.dp, shape.padding)
    }

    // ── Enum completeness ────────────────────────────────────────────────

    @Test
    fun tooltipPosition_values() {
        val values = TooltipPosition.entries
        assertEquals(5, values.size)
        assertTrue(values.contains(TooltipPosition.AUTO))
        assertTrue(values.contains(TooltipPosition.TOP))
        assertTrue(values.contains(TooltipPosition.BOTTOM))
        assertTrue(values.contains(TooltipPosition.START))
        assertTrue(values.contains(TooltipPosition.END))
    }

    @Test
    fun highlightAnimation_values() {
        val values = HighlightAnimation.entries
        assertEquals(6, values.size)
        assertTrue(values.contains(HighlightAnimation.NONE))
        assertTrue(values.contains(HighlightAnimation.PULSE))
        assertTrue(values.contains(HighlightAnimation.GLOW))
        assertTrue(values.contains(HighlightAnimation.RIPPLE))
        assertTrue(values.contains(HighlightAnimation.SHIMMER))
        assertTrue(values.contains(HighlightAnimation.BOUNCE))
    }

    @Test
    fun connectorStyle_values() {
        val values = ConnectorStyle.entries
        assertEquals(6, values.size)
        assertTrue(values.contains(ConnectorStyle.AUTO))
        assertTrue(values.contains(ConnectorStyle.DIRECT))
        assertTrue(values.contains(ConnectorStyle.HORIZONTAL))
        assertTrue(values.contains(ConnectorStyle.VERTICAL))
        assertTrue(values.contains(ConnectorStyle.ELBOW))
        assertTrue(values.contains(ConnectorStyle.CURVED))
    }

    @Test
    fun default_connectorEndStyle_is_DOT() {
        val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
        assertEquals(ConnectorEndStyle.DOT, target.connectorEndStyle)
    }

    @Test
    fun connectorEndStyle_values() {
        val values = ConnectorEndStyle.entries
        assertEquals(4, values.size)
        assertTrue(values.contains(ConnectorEndStyle.DOT))
        assertTrue(values.contains(ConnectorEndStyle.ARROW))
        assertTrue(values.contains(ConnectorEndStyle.NONE))
        assertTrue(values.contains(ConnectorEndStyle.CUSTOM))
    }

    // ── TargetTapBehavior ──────────────────────────────────────────────────

    @Test
    fun default_targetTapBehavior_is_PASS_THROUGH() {
        val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
        assertEquals(TargetTapBehavior.PASS_THROUGH, target.targetTapBehavior)
    }

    @Test
    fun targetTapBehavior_enum_has_3_entries() {
        val values = TargetTapBehavior.entries
        assertEquals(3, values.size)
        assertTrue(values.contains(TargetTapBehavior.PASS_THROUGH))
        assertTrue(values.contains(TargetTapBehavior.ADVANCE))
        assertTrue(values.contains(TargetTapBehavior.BOTH))
    }

    // ── CutoutShape sealed hierarchy ─────────────────────────────────────

    @Test
    fun cutoutShape_variants_are_distinct_types() {
        val circle: CutoutShape = CutoutShape.Circle()
        val roundedRect: CutoutShape = CutoutShape.RoundedRect()
        val rect: CutoutShape = CutoutShape.Rect()
        val squircle: CutoutShape = CutoutShape.Squircle()
        val star: CutoutShape = CutoutShape.Star()

        assertIs<CutoutShape.Circle>(circle)
        assertIs<CutoutShape.RoundedRect>(roundedRect)
        assertIs<CutoutShape.Rect>(rect)
        assertIs<CutoutShape.Squircle>(squircle)
        assertIs<CutoutShape.Star>(star)
    }
}
