package io.luminos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Coordinates between different overlay types (dialogs, bottom sheets, coachmarks)
 * to prevent timing conflicts and ensure proper priority.
 *
 * This solves the problem of coachmarks accidentally appearing when dialogs are showing,
 * without requiring manual checks for each dialog type.
 *
 * Usage at app/screen level:
 * ```
 * val overlayCoordinator = rememberOverlayCoordinator()
 * CompositionLocalProvider(LocalOverlayCoordinator provides overlayCoordinator) {
 *     // Dialogs automatically register via DialogOverlayEffect
 *     // Coachmarks automatically check via CoachmarkController
 * }
 * ```
 *
 * Usage in dialogs:
 * ```
 * if (showDialog) {
 *     DialogOverlayEffect()  // Auto-registers while dialog is showing
 *     AlertDialog(...)
 * }
 * ```
 */
@Stable
class OverlayCoordinator {
    private val _activeDialogCount = MutableStateFlow(0)

    /**
     * Number of currently active dialogs/bottom sheets.
     * Coachmarks should not show when this is > 0.
     */
    val activeDialogCount: StateFlow<Int> = _activeDialogCount.asStateFlow()

    /**
     * Returns true if any dialog or bottom sheet is currently showing.
     */
    val isDialogShowing: Boolean
        get() = _activeDialogCount.value > 0

    private val counter = atomic(0)

    /**
     * Registers a dialog as active. Call when dialog becomes visible.
     * Returns a token to use when unregistering.
     */
    fun registerDialog(): Int {
        val token = counter.incrementAndGet()
        _activeDialogCount.value++
        return token
    }

    /**
     * Unregisters a dialog. Call when dialog is dismissed.
     */
    fun unregisterDialog() {
        _activeDialogCount.value = maxOf(0, _activeDialogCount.value - 1)
    }
}

/**
 * Creates and remembers an [OverlayCoordinator] instance.
 */
@Composable
fun rememberOverlayCoordinator(): OverlayCoordinator = remember { OverlayCoordinator() }

/**
 * CompositionLocal for providing [OverlayCoordinator] down the composition tree.
 */
val LocalOverlayCoordinator = staticCompositionLocalOf<OverlayCoordinator?> { null }

/**
 * Effect that automatically registers/unregisters a dialog with the [OverlayCoordinator].
 *
 * Place this inside any dialog's content block to automatically track it:
 * ```
 * if (showDialog) {
 *     DialogOverlayEffect()
 *     AlertDialog(
 *         onDismissRequest = { showDialog = false },
 *         ...
 *     )
 * }
 * ```
 *
 * The dialog will be automatically unregistered when dismissed or when
 * the composable leaves composition.
 */
@Composable
fun DialogOverlayEffect() {
    val coordinator = LocalOverlayCoordinator.current ?: return

    DisposableEffect(coordinator) {
        coordinator.registerDialog()
        onDispose {
            coordinator.unregisterDialog()
        }
    }
}
