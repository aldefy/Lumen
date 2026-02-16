package io.luminos

/**
 * Reason why a coachmark was dismissed.
 */
enum class DismissReason {
    /** User tapped the scrim (outside the cutout). */
    SCRIM_TAP,
    /** User tapped the skip button. */
    SKIP_BUTTON,
    /** User pressed the back/escape button. */
    BACK_PRESS,
    /** Dismissed programmatically via [CoachmarkController.dismiss]. */
    PROGRAMMATIC,
    /** Target was suppressed by "Don't Show Again" repository check. */
    SUPPRESSED,
    /** A dialog appeared and auto-dismissed the coachmark. */
    DIALOG_INTERRUPTED,
}

/**
 * Analytics callbacks for coachmark lifecycle events.
 *
 * All callbacks default to no-ops. Attach an instance to [CoachmarkHost]
 * to receive analytics events without changing dismiss/step behavior.
 *
 * @property onShow Fired when a coachmark step becomes visible.
 * @property onDismiss Fired when a coachmark is dismissed (with reason).
 * @property onAdvance Fired when the user advances from one step to the next.
 * @property onComplete Fired when the entire coachmark flow is completed.
 */
data class CoachmarkAnalytics(
    val onShow: (targetId: String, stepIndex: Int, totalSteps: Int) -> Unit = { _, _, _ -> },
    val onDismiss: (targetId: String, stepIndex: Int, totalSteps: Int, reason: DismissReason) -> Unit = { _, _, _, _ -> },
    val onAdvance: (fromTargetId: String, toTargetId: String?, stepIndex: Int, totalSteps: Int) -> Unit = { _, _, _, _ -> },
    val onComplete: (totalSteps: Int) -> Unit = { _ -> },
)
