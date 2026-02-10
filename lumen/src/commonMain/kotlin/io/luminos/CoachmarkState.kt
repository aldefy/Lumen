package io.luminos

import androidx.compose.runtime.Immutable

/**
 * Represents the current state of the coachmark overlay.
 */
@Immutable
sealed interface CoachmarkState {
    /**
     * No coachmark is currently visible.
     */
    data object Hidden : CoachmarkState

    /**
     * A single coachmark is being displayed.
     *
     * @property target The target element to highlight
     * @property currentStep Current step number (1-indexed for display)
     * @property totalSteps Total number of steps in the sequence
     */
    @Immutable
    data class Showing(
        val target: CoachmarkTarget,
        val currentStep: Int = 1,
        val totalSteps: Int = 1,
    ) : CoachmarkState

    /**
     * A sequence of coachmarks is being displayed.
     *
     * @property targets List of all targets in the sequence
     * @property currentIndex Current target index (0-indexed)
     */
    @Immutable
    data class Sequence(
        val targets: List<CoachmarkTarget>,
        val currentIndex: Int,
    ) : CoachmarkState {
        val currentTarget: CoachmarkTarget
            get() = targets[currentIndex]

        val currentStep: Int
            get() = currentIndex + 1

        val totalSteps: Int
            get() = targets.size

        val hasNext: Boolean
            get() = currentIndex < targets.size - 1

        val hasPrevious: Boolean
            get() = currentIndex > 0
    }
}
