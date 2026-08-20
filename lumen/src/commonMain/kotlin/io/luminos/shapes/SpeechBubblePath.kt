package io.luminos.shapes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * A rounded-rect [Shape] with a small speech-bubble tail carved into one edge, pointing at
 * [tailAnchorX] — the tail and the card body are one continuous outline, not two shapes drawn
 * on top of each other, so the tail always reads as part of the card (no seam, and it
 * automatically follows the card's own background/border/shadow).
 *
 * The tail lives *inside* the shape's own bounds (`size`, as passed by [createOutline]) rather
 * than bumping outward past them — [androidx.compose.ui.draw.clip] always bounds to the
 * composable's actual layout size, so anything drawn outside `size` would simply be cut off.
 * The caller is expected to reserve [tailHeight] of extra space in that direction (e.g. via
 * padding) before this shape is applied — see [CoachmarkConfig.tooltipTailInset].
 *
 * @param cornerRadius Corner radius of the rounded-rect body, in px.
 * @param tailWidth Width of the tail's base, in px.
 * @param tailHeight How far the tail extends from the card body's edge, in px. Must be less
 *   than the extra space the caller reserved in that direction, or the peak gets clipped.
 * @param tailAnchorX Horizontal position of the tail's center, in px, relative to the shape's
 *   left edge. Clamped inward so the tail's base never overlaps the corner radius.
 * @param tailOnBottom `true` puts the tail on the bottom edge (tooltip above the target,
 *   pointing down); `false` puts it on the top edge (tooltip below the target, pointing up).
 */
class SpeechBubbleShape(
    private val cornerRadius: Float,
    private val tailWidth: Float,
    private val tailHeight: Float,
    private val tailAnchorX: Float,
    private val tailOnBottom: Boolean,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        if (tailHeight <= 0f || tailWidth <= 0f) {
            // Degenerates to a plain rounded rect when there's no tail to draw — callers can
            // pass tailHeight = 0f to disable the tail without switching shapes.
            return RoundedCornerShape(cornerRadius.coerceAtLeast(0f))
                .createOutline(size, layoutDirection, density)
        }
        // The card body occupies size minus the tail's reserved strip; the tail then fills
        // that strip, growing from the body's edge toward the shape's own outer edge — both
        // stay within `size`, so nothing here relies on clip drawing outside its own bounds.
        val bodyBounds = if (tailOnBottom) {
            Rect(0f, 0f, size.width, size.height - tailHeight)
        } else {
            Rect(0f, tailHeight, size.width, size.height)
        }
        val path = speechBubblePath(
            bounds = bodyBounds,
            cornerRadius = cornerRadius,
            tailWidth = tailWidth,
            tailHeight = tailHeight,
            tailAnchorX = tailAnchorX,
            tailOnBottom = tailOnBottom,
        )
        return Outline.Generic(path)
    }
}

/**
 * Builds the speech-bubble outline: a rounded rect for [bounds] with a rounded hill-shaped
 * tail growing from the top or bottom edge, centered at [tailAnchorX] (clamped so its base
 * stays clear of the corner radius on both sides), extending [tailHeight] past that edge.
 */
fun speechBubblePath(
    bounds: Rect,
    cornerRadius: Float,
    tailWidth: Float,
    tailHeight: Float,
    tailAnchorX: Float,
    tailOnBottom: Boolean,
): Path {
    val r = cornerRadius.coerceIn(0f, minOf(bounds.width, bounds.height) / 2f)
    val halfTail = tailWidth / 2f
    val anchorX = tailAnchorX.coerceIn(
        bounds.left + r + halfTail,
        (bounds.right - r - halfTail).coerceAtLeast(bounds.left + r + halfTail),
    )
    val tailLeft = anchorX - halfTail
    val tailRight = anchorX + halfTail
    val quarterTail = halfTail / 2f

    return Path().apply {
        if (tailOnBottom) {
            // Clockwise from top-left: top edge, right corner+edge, bottom edge with the
            // tail carved in (right-to-left, since we're going counter to the tail's own
            // left-to-right base), left corner+edge, back to top-left.
            moveTo(bounds.left + r, bounds.top)
            lineTo(bounds.right - r, bounds.top)
            arcTo(Rect(bounds.right - 2 * r, bounds.top, bounds.right, bounds.top + 2 * r), -90f, 90f, false)
            lineTo(bounds.right, bounds.bottom - r)
            arcTo(Rect(bounds.right - 2 * r, bounds.bottom - 2 * r, bounds.right, bounds.bottom), 0f, 90f, false)
            lineTo(tailRight, bounds.bottom)
            // Tail: hill shape growing downward from the bottom edge, peak below the edge.
            cubicTo(
                tailRight - quarterTail, bounds.bottom,
                anchorX + quarterTail, bounds.bottom + tailHeight,
                anchorX, bounds.bottom + tailHeight,
            )
            cubicTo(
                anchorX - quarterTail, bounds.bottom + tailHeight,
                tailLeft + quarterTail, bounds.bottom,
                tailLeft, bounds.bottom,
            )
            lineTo(bounds.left + r, bounds.bottom)
            arcTo(Rect(bounds.left, bounds.bottom - 2 * r, bounds.left + 2 * r, bounds.bottom), 90f, 90f, false)
            lineTo(bounds.left, bounds.top + r)
            arcTo(Rect(bounds.left, bounds.top, bounds.left + 2 * r, bounds.top + 2 * r), 180f, 90f, false)
            close()
        } else {
            // Tail on the top edge, peak above it, pointing up.
            moveTo(bounds.left + r, bounds.top)
            lineTo(tailLeft, bounds.top)
            cubicTo(
                tailLeft + quarterTail, bounds.top,
                anchorX - quarterTail, bounds.top - tailHeight,
                anchorX, bounds.top - tailHeight,
            )
            cubicTo(
                anchorX + quarterTail, bounds.top - tailHeight,
                tailRight - quarterTail, bounds.top,
                tailRight, bounds.top,
            )
            lineTo(bounds.right - r, bounds.top)
            arcTo(Rect(bounds.right - 2 * r, bounds.top, bounds.right, bounds.top + 2 * r), -90f, 90f, false)
            lineTo(bounds.right, bounds.bottom - r)
            arcTo(Rect(bounds.right - 2 * r, bounds.bottom - 2 * r, bounds.right, bounds.bottom), 0f, 90f, false)
            lineTo(bounds.left + r, bounds.bottom)
            arcTo(Rect(bounds.left, bounds.bottom - 2 * r, bounds.left + 2 * r, bounds.bottom), 90f, 90f, false)
            lineTo(bounds.left, bounds.top + r)
            arcTo(Rect(bounds.left, bounds.top, bounds.left + 2 * r, bounds.top + 2 * r), 180f, 90f, false)
            close()
        }
    }
}
