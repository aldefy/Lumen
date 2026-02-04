package io.luminos.shapes

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import kotlin.math.abs
import kotlin.math.pow

/**
 * Creates an iOS-style squircle (superellipse) path.
 *
 * A squircle is a shape between a square and a circle, characterized by
 * smoother corners than a rounded rectangle. It's defined by the superellipse
 * equation: |x/a|^n + |y/b|^n = 1, where n > 2.
 *
 * @param bounds The bounding rectangle for the squircle
 * @param cornerRadius The corner radius (controls how "round" the corners are)
 * @param smoothness The superellipse exponent (higher = more square-like, default 4.0 for iOS-style)
 */
fun createSquirclePath(
    bounds: Rect,
    cornerRadius: Float,
    smoothness: Float = 4f,
): Path {
    val path = Path()

    val width = bounds.width
    val height = bounds.height
    val centerX = bounds.center.x
    val centerY = bounds.center.y

    // Limit corner radius to half of the smaller dimension
    val maxRadius = minOf(width, height) / 2
    val radius = cornerRadius.coerceIn(0f, maxRadius)

    // For very small radius, just draw a rectangle
    if (radius < 1f) {
        path.addRect(bounds)
        return path
    }

    // For radius approaching half the size, draw a superellipse
    val halfWidth = width / 2
    val halfHeight = height / 2

    // Calculate the effective a and b parameters for the superellipse
    // We need to blend between rectangle corners and smooth superellipse
    val a = halfWidth
    val b = halfHeight

    // Number of segments for smooth curve
    val segments = 100

    // Precompute exponent
    val exponent = (2.0 / smoothness).toDouble()

    // Generate points along the superellipse
    for (i in 0..segments) {
        val t = (i.toDouble() / segments) * 2 * Math.PI
        val cosT = kotlin.math.cos(t)
        val sinT = kotlin.math.sin(t)

        // Superellipse parametric equations with sign preservation
        val x = centerX + a * sign(cosT) * abs(cosT).pow(exponent).toFloat()
        val y = centerY + b * sign(sinT) * abs(sinT).pow(exponent).toFloat()

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }

    path.close()
    return path
}

/**
 * Creates a squircle path with padding around the given bounds.
 *
 * @param bounds The inner bounds (the actual target element)
 * @param padding Additional padding to add around the bounds
 * @param cornerRadius The corner radius for the squircle
 * @param smoothness The superellipse exponent
 */
fun createPaddedSquirclePath(
    bounds: Rect,
    padding: Float,
    cornerRadius: Float,
    smoothness: Float = 4f,
): Path {
    val paddedBounds =
        Rect(
            left = bounds.left - padding,
            top = bounds.top - padding,
            right = bounds.right + padding,
            bottom = bounds.bottom + padding,
        )
    return createSquirclePath(paddedBounds, cornerRadius, smoothness)
}

private fun sign(value: Float): Float =
    when {
        value > 0 -> 1f
        value < 0 -> -1f
        else -> 0f
    }

private fun sign(value: Double): Float =
    when {
        value > 0 -> 1f
        value < 0 -> -1f
        else -> 0f
    }
