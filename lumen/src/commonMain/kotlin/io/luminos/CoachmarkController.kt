package io.luminos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Controller for managing coachmark state and target registration.
 *
 * Use [rememberCoachmarkController] to create and remember an instance in Compose.
 *
 * @param overlayCoordinator Optional coordinator to prevent coachmarks from showing
 *        when dialogs are active. When provided, [show] and [showSequence] will
 *        silently no-op if any dialog is currently showing.
 */
@Stable
class CoachmarkController(
    private val overlayCoordinator: OverlayCoordinator? = null,
) {
    private val _state = MutableStateFlow<CoachmarkState>(CoachmarkState.Hidden)
    val state: StateFlow<CoachmarkState> = _state.asStateFlow()

    private val registeredTargets = mutableMapOf<String, Rect>()
    private val targetVisibility = mutableMapOf<String, Boolean>()

    /** Viewport bounds for visibility checking (set by CoachmarkHost) */
    private var viewportBounds: Rect? = null

    /** Whether the container (e.g., LazyColumn) is currently scrolling */
    var isScrolling: Boolean by mutableStateOf(false)

    /**
     * Global toggle for coachmarks. When false, [show] and [showSequence] silently no-op.
     *
     * Use this to disable coachmarks based on app context:
     * - Deep link launches
     * - Test/debug modes
     * - Feature flags
     * - User preferences
     *
     * Example:
     * ```
     * LaunchedEffect(isDeepLink) {
     *     coachmarkController.enabled = !isDeepLink
     * }
     * ```
     */
    var enabled: Boolean by mutableStateOf(true)

    /**
     * Returns true if a dialog is currently blocking coachmarks.
     */
    val isBlockedByDialog: Boolean
        get() = overlayCoordinator?.isDialogShowing == true

    /**
     * Registers a target element's bounds for later use.
     * Called by [coachmarkTarget] modifier when layout changes.
     */
    fun registerTarget(
        id: String,
        bounds: Rect,
    ) {
        registeredTargets[id] = bounds
        targetVisibility[id] = isWithinViewport(bounds)
        updateTargetBoundsIfShowing(id, bounds)
    }

    /**
     * Unregisters a target when it leaves composition.
     */
    fun unregisterTarget(id: String) {
        registeredTargets.remove(id)
    }

    /**
     * Gets the current bounds for a registered target.
     */
    fun getTargetBounds(id: String): Rect? = registeredTargets[id]

    /**
     * Sets the viewport bounds for visibility checking.
     * Called by CoachmarkHost when layout changes.
     */
    fun setViewportBounds(bounds: Rect) {
        viewportBounds = bounds
        // Update visibility for all registered targets
        registeredTargets.forEach { (id, targetBounds) ->
            targetVisibility[id] = isWithinViewport(targetBounds)
        }
    }

    /**
     * Checks if a target is currently visible within the viewport.
     */
    fun isTargetVisible(id: String): Boolean {
        val bounds = registeredTargets[id] ?: return false
        return isWithinViewport(bounds)
    }

    /**
     * Checks if the current coachmark target is visible and ready to show.
     * Returns false if target is outside viewport or scroll is in progress.
     */
    fun isCurrentTargetReady(): Boolean {
        if (isScrolling) return false

        val currentTargetId = when (val s = _state.value) {
            is CoachmarkState.Showing -> s.target.id
            is CoachmarkState.Sequence -> s.currentTarget.id
            CoachmarkState.Hidden -> return true
        }
        return isTargetVisible(currentTargetId)
    }

    private fun isWithinViewport(bounds: Rect): Boolean {
        val viewport = viewportBounds ?: return true // If no viewport set, assume visible
        // Check if target overlaps with viewport (at least partially visible)
        return bounds.overlaps(viewport) && bounds.width > 0 && bounds.height > 0
    }

    /**
     * Shows a single coachmark for the specified target.
     *
     * Will silently no-op if:
     * - [enabled] is false (e.g., deep link context)
     * - A dialog is currently showing (via [OverlayCoordinator])
     *
     * @param target The target to highlight
     * @return true if the coachmark was shown, false if blocked
     */
    fun show(target: CoachmarkTarget): Boolean {
        if (!enabled) {
            return false
        }

        if (isBlockedByDialog) {
            return false
        }

        val bounds = registeredTargets[target.id] ?: target.bounds
        _state.value =
            CoachmarkState.Showing(
                target = target.copy(bounds = bounds),
                currentStep = 1,
                totalSteps = 1,
            )
        return true
    }

    /**
     * Shows a sequence of coachmarks.
     * The user will progress through each target in order.
     *
     * Will silently no-op if:
     * - [enabled] is false (e.g., deep link context)
     * - A dialog is currently showing (via [OverlayCoordinator])
     * - [targets] is empty
     *
     * @param targets List of targets to show in sequence
     * @return true if the sequence was shown, false if blocked or empty
     */
    fun showSequence(targets: List<CoachmarkTarget>): Boolean {
        if (targets.isEmpty()) return false

        if (!enabled) {
            return false
        }

        if (isBlockedByDialog) {
            return false
        }

        val updatedTargets =
            targets.map { target ->
                val bounds = registeredTargets[target.id] ?: target.bounds
                target.copy(bounds = bounds)
            }

        _state.value =
            CoachmarkState.Sequence(
                targets = updatedTargets,
                currentIndex = 0,
            )
        return true
    }

    /**
     * Advances to the next coachmark in a sequence.
     * If on the last item or showing a single target, dismisses the overlay.
     */
    fun next() {
        _state.update { currentState ->
            when (currentState) {
                is CoachmarkState.Sequence -> {
                    if (currentState.hasNext) {
                        currentState.copy(currentIndex = currentState.currentIndex + 1)
                    } else {
                        CoachmarkState.Hidden
                    }
                }
                is CoachmarkState.Showing -> CoachmarkState.Hidden
                CoachmarkState.Hidden -> CoachmarkState.Hidden
            }
        }
    }

    /**
     * Goes back to the previous coachmark in a sequence.
     * Does nothing if on the first item or showing a single target.
     */
    fun previous() {
        _state.update { currentState ->
            when (currentState) {
                is CoachmarkState.Sequence -> {
                    if (currentState.hasPrevious) {
                        currentState.copy(currentIndex = currentState.currentIndex - 1)
                    } else {
                        currentState
                    }
                }
                else -> currentState
            }
        }
    }

    /**
     * Dismisses the coachmark overlay immediately.
     */
    fun dismiss() {
        _state.value = CoachmarkState.Hidden
    }

    /**
     * Updates the bounds of a currently showing target.
     * This handles cases where layout changes while coachmark is visible.
     */
    private fun updateTargetBoundsIfShowing(
        id: String,
        bounds: Rect,
    ) {
        _state.update { currentState ->
            when (currentState) {
                is CoachmarkState.Showing -> {
                    if (currentState.target.id == id) {
                        currentState.copy(target = currentState.target.copy(bounds = bounds))
                    } else {
                        currentState
                    }
                }
                is CoachmarkState.Sequence -> {
                    val updatedTargets =
                        currentState.targets.map { target ->
                            if (target.id == id) target.copy(bounds = bounds) else target
                        }
                    currentState.copy(targets = updatedTargets)
                }
                CoachmarkState.Hidden -> currentState
            }
        }
    }
}

/**
 * Creates and remembers a [CoachmarkController] instance.
 *
 * @param overlayCoordinator Optional coordinator to prevent coachmarks when dialogs are showing.
 *        If provided, coachmarks will automatically be blocked when any dialog is active.
 *        Pass [LocalOverlayCoordinator.current] for automatic dialog coordination.
 */
@Composable
fun rememberCoachmarkController(
    overlayCoordinator: OverlayCoordinator? = LocalOverlayCoordinator.current,
): CoachmarkController =
    remember(overlayCoordinator) {
        CoachmarkController(overlayCoordinator)
    }

/**
 * CompositionLocal for providing [CoachmarkController] down the composition tree.
 *
 * Usage at screen level:
 * ```
 * val controller = rememberCoachmarkController()
 * CompositionLocalProvider(LocalCoachmarkController provides controller) {
 *     // Children can access via LocalCoachmarkController.current
 * }
 * ```
 *
 * Usage in child composables:
 * ```
 * val controller = LocalCoachmarkController.current
 * if (controller != null) {
 *     Modifier.coachmarkTarget(controller, "my_target")
 * }
 * ```
 */
val LocalCoachmarkController = staticCompositionLocalOf<CoachmarkController?> { null }
