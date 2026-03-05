# Design: Retry Skipped Coachmark Targets

## Problem

When a coachmark sequence runs while LazyColumn targets are scrolled off-screen, those targets are permanently skipped via `skipCurrentIfNotVisible()`. The user never sees those coachmarks, even if they scroll the targets into view later.

## Solution

Reorder the targets list in-place (Approach A). When an off-screen target is encountered and `retrySkippedTargets = true`, move it to the end of the sequence instead of advancing past it. The existing `scrollRequester` auto-scroll logic handles the appended target when the sequence reaches it.

## API Surface

New field in `CoachmarkConfig`:

```kotlin
val retrySkippedTargets: Boolean = false
```

Opt-in. Only effective when `waitForVisibility = true`. Default preserves current behavior.

## State Model

`CoachmarkState.Sequence` is unchanged — same `targets: List<CoachmarkTarget>` and `currentIndex: Int`. Reordering produces a new `Sequence` with a reordered list via `copy()`.

Example:
```
Before: [A, B*, C, D]  currentIndex=1, B is off-screen
After:  [A, C, D, B]   currentIndex=1, now points to C
```

## Core Logic

### CoachmarkController.skipCurrentIfNotVisible

Add `retry: Boolean = false` parameter:

- `retry = false` (default): Current behavior. Advance `currentIndex` or dismiss.
- `retry = true`: Remove current target from its position, append to end of list. `currentIndex` stays the same (now points to the next target). Edge case: if the skipped target was the last item, `currentIndex` clamps to `reordered.size - 1`.

### CoachmarkScrim LaunchedEffect

The auto-scroll/skip `LaunchedEffect(currentTargetId)` passes `config.retrySkippedTargets` to `skipCurrentIfNotVisible(retry = ...)`. Both call sites (no-scroller branch and scroll-failed branch) get the same change.

## Infinite Loop Guard

A target that can never become visible (e.g., removed from the LazyColumn) would loop forever. Guard with a `remember { mutableSetOf<String>() }` in the scrim tracking retried target IDs:

- First skip of target X: append to end + add X to set.
- Second skip of target X: skip permanently (current behavior).

## What Doesn't Change

- `next()`, `previous()`, `hasNext`, `hasPrevious` — work as-is
- Step indicator — total count unchanged (reorder, not removal)
- `scrollRequester` — invoked naturally when appended step becomes current
- Analytics callbacks — fire normally when appended target shows

## Files Modified

1. `CoachmarkScrim.kt` — add `retrySkippedTargets` to `CoachmarkConfig`, add retry set + pass retry flag
2. `CoachmarkController.kt` — update `skipCurrentIfNotVisible` with retry logic
