package io.luminos.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Creates a star-shaped path.
 *
 * @param center The center point of the star
 * @param outerRadius The radius to the outer points of the star
 * @param innerRadius The radius to the inner points (between the star points)
 * @param points Number of points on the star (default 5)
 */
fun createStarPath(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    points: Int = 5,
): Path {
    val path = Path()
    val angleStep = PI / points
    // Start from top (rotate -90 degrees so first point is at top)
    val startAngle = -PI / 2

    for (i in 0 until points * 2) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = startAngle + i * angleStep
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()

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
 * Creates a star path with padding around the given bounds.
 *
 * @param bounds The inner bounds (the actual target element)
 * @param padding Additional padding to add around the bounds
 * @param points Number of points on the star
 * @param innerRadiusRatio Ratio of inner radius to outer radius (0.0-1.0)
 */
fun createPaddedStarPath(
    bounds: Rect,
    padding: Float,
    points: Int = 5,
    innerRadiusRatio: Float = 0.5f,
): Path {
    val center = bounds.center
    val outerRadius = min(bounds.width, bounds.height) / 2 + padding
    val innerRadius = outerRadius * innerRadiusRatio.coerceIn(0.1f, 0.9f)
    return createStarPath(center, outerRadius, innerRadius, points)
}

/**
 * Creates a star path with padding and scale support for animations.
 *
 * @param bounds The inner bounds (the actual target element)
 * @param padding Additional padding to add around the bounds
 * @param points Number of points on the star
 * @param innerRadiusRatio Ratio of inner radius to outer radius (0.0-1.0)
 * @param scale Scale factor for animations (1.0 = normal size)
 */
fun createScaledStarPath(
    bounds: Rect,
    padding: Float,
    points: Int = 5,
    innerRadiusRatio: Float = 0.5f,
    scale: Float = 1f,
): Path {
    val center = bounds.center
    val baseOuterRadius = min(bounds.width, bounds.height) / 2 + padding
    val outerRadius = baseOuterRadius * scale
    val innerRadius = outerRadius * innerRadiusRatio.coerceIn(0.1f, 0.9f)
    return createStarPath(center, outerRadius, innerRadius, points)
}
