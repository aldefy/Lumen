package io.luminos

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * Modifier that registers an element as a coachmark target.
 *
 * When applied to a composable, it tracks the element's position and size
 * in root coordinates, and registers it with the [CoachmarkController].
 *
 * Example usage:
 * ```
 * IconButton(
 *     onClick = { /* ... */ },
 *     modifier = Modifier.coachmarkTarget(
 *         controller = coachmarkController,
 *         id = "settings_icon"
 *     )
 * ) {
 *     Icon(Icons.Default.Settings, contentDescription = "Settings")
 * }
 * ```
 *
 * @param controller The [CoachmarkController] to register with
 * @param id Unique identifier for this target
 */
fun Modifier.coachmarkTarget(
    controller: CoachmarkController,
    id: String,
): Modifier =
    composed {
        var bounds by remember { mutableStateOf<Rect?>(null) }

        LaunchedEffect(bounds) {
            bounds?.let { controller.registerTarget(id, it) }
        }

        DisposableEffect(id) {
            onDispose { controller.unregisterTarget(id) }
        }

        this.onGloballyPositioned { coordinates ->
            val newBounds = coordinates.boundsInRoot()
            if (newBounds != bounds) {
                bounds = newBounds
            }
        }
    }
