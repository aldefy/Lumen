# Retry Skipped Coachmarks Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** When `retrySkippedTargets = true`, off-screen coachmark targets are moved to the end of the sequence instead of permanently skipped, so users see them when they scroll back.

**Architecture:** Add a `retry` parameter to `skipCurrentIfNotVisible()` that reorders the targets list instead of advancing past them. An infinite-loop guard in the scrim prevents retrying the same target more than once. New opt-in `CoachmarkConfig.retrySkippedTargets` flag controls the behavior.

**Tech Stack:** Kotlin Multiplatform, Jetpack Compose, kotlinx-coroutines-test

---

### Task 1: Add retry logic to CoachmarkController.skipCurrentIfNotVisible

**Files:**
- Modify: `lumen/src/commonMain/kotlin/io/luminos/CoachmarkController.kt:308-322`
- Test: `lumen/src/commonTest/kotlin/io/luminos/CoachmarkControllerTest.kt`

**Step 1: Write failing tests for retry behavior**

Add these tests after the existing `skipCurrentIfNotVisible_on_Hidden_is_noop` test (after line 631) in `CoachmarkControllerTest.kt`:

```kotlin
@Test
fun skipCurrentIfNotVisible_retry_moves_target_to_end() = runTest {
    val controller = CoachmarkController()
    controller.showSequence(listOf(target("a"), target("b"), target("c"), target("d")))
    controller.next() // index 0 -> 1, now on "b"
    controller.skipCurrentIfNotVisible(retry = true)
    val state = controller.state.value
    assertIs<CoachmarkState.Sequence>(state)
    // "b" moved to end: [a, c, d, b], index stays at 1 -> now points to "c"
    assertEquals("c", state.currentTarget.id)
    assertEquals(1, state.currentIndex)
    assertEquals(listOf("a", "c", "d", "b"), state.targets.map { it.id })
}

@Test
fun skipCurrentIfNotVisible_retry_on_last_item_keeps_sequence() = runTest {
    val controller = CoachmarkController()
    controller.showSequence(listOf(target("a"), target("b"), target("c")))
    controller.next() // -> 1
    controller.next() // -> 2 (last, "c")
    controller.skipCurrentIfNotVisible(retry = true)
    val state = controller.state.value
    assertIs<CoachmarkState.Sequence>(state)
    // "c" removed and re-appended: [a, b, c], index clamped to 2
    assertEquals("c", state.currentTarget.id)
    assertEquals(2, state.currentIndex)
}

@Test
fun skipCurrentIfNotVisible_retry_false_preserves_current_behavior() = runTest {
    val controller = CoachmarkController()
    controller.showSequence(listOf(target("a"), target("b"), target("c")))
    controller.skipCurrentIfNotVisible(retry = false)
    val state = controller.state.value
    assertIs<CoachmarkState.Sequence>(state)
    assertEquals(1, state.currentIndex)
    assertEquals("b", state.currentTarget.id)
}

@Test
fun skipCurrentIfNotVisible_retry_on_Showing_still_dismisses() = runTest {
    val controller = CoachmarkController()
    controller.show(target("t1"))
    controller.skipCurrentIfNotVisible(retry = true)
    assertIs<CoachmarkState.Hidden>(controller.state.value)
}
```

**Step 2: Run tests to verify they fail**

Run: `cd /Users/adit/Documents/AndroidProjects/Lumen && ./gradlew :lumen:jvmTest --tests "io.luminos.CoachmarkControllerTest" 2>&1 | tail -20`
Expected: FAIL — `skipCurrentIfNotVisible` does not accept a `retry` parameter.

**Step 3: Implement the retry parameter**

In `lumen/src/commonMain/kotlin/io/luminos/CoachmarkController.kt`, replace lines 303-322:

```kotlin
/**
 * Skips the current target if it's not visible.
 * In a sequence: advances to next step (or dismisses if last).
 * For single target: dismisses.
 *
 * @param retry If true, moves the current target to the end of the sequence
 *   instead of permanently skipping it. Only applies to Sequence state.
 */
internal fun skipCurrentIfNotVisible(retry: Boolean = false) {
    _state.update { currentState ->
        when (currentState) {
            is CoachmarkState.Sequence -> {
                if (retry) {
                    val reordered = currentState.targets.toMutableList()
                    val skipped = reordered.removeAt(currentState.currentIndex)
                    reordered.add(skipped)
                    val newIndex = if (currentState.currentIndex >= reordered.size) {
                        reordered.size - 1
                    } else {
                        currentState.currentIndex
                    }
                    currentState.copy(targets = reordered, currentIndex = newIndex)
                } else {
                    if (currentState.hasNext) {
                        currentState.copy(currentIndex = currentState.currentIndex + 1)
                    } else {
                        CoachmarkState.Hidden
                    }
                }
            }
            is CoachmarkState.Showing -> CoachmarkState.Hidden
            CoachmarkState.Hidden -> CoachmarkState.Hidden
        }
    }
}
```

**Step 4: Run tests to verify they pass**

Run: `cd /Users/adit/Documents/AndroidProjects/Lumen && ./gradlew :lumen:jvmTest --tests "io.luminos.CoachmarkControllerTest" 2>&1 | tail -20`
Expected: All tests PASS.

**Step 5: Commit**

```bash
git add lumen/src/commonMain/kotlin/io/luminos/CoachmarkController.kt lumen/src/commonTest/kotlin/io/luminos/CoachmarkControllerTest.kt
git commit -m "feat: add retry parameter to skipCurrentIfNotVisible"
```

---

### Task 2: Add retrySkippedTargets to CoachmarkConfig

**Files:**
- Modify: `lumen/src/commonMain/kotlin/io/luminos/CoachmarkScrim.kt:167-228`

**Step 1: Add the config field**

In `CoachmarkScrim.kt`, add this field after `scrollTimeout` (after line 219):

```kotlin
/** When true and waitForVisibility is true, off-screen targets are moved to the end
 *  of the sequence instead of permanently skipped. Default: false. */
val retrySkippedTargets: Boolean = false,
```

**Step 2: Build to verify it compiles**

Run: `cd /Users/adit/Documents/AndroidProjects/Lumen && ./gradlew :lumen:compileKotlinMetadata 2>&1 | tail -10`
Expected: BUILD SUCCESSFUL.

**Step 3: Commit**

```bash
git add lumen/src/commonMain/kotlin/io/luminos/CoachmarkScrim.kt
git commit -m "feat: add retrySkippedTargets config flag"
```

---

### Task 3: Wire retry flag and infinite loop guard in CoachmarkScrim

**Files:**
- Modify: `lumen/src/commonMain/kotlin/io/luminos/CoachmarkScrim.kt:286-329`

**Step 1: Add the infinite loop guard**

After the `isReadyToShow` declaration (line 288), add:

```kotlin
// Track targets that have already been retried once to prevent infinite loops
val retriedTargets = remember { mutableSetOf<String>() }
```

**Step 2: Pass retry flag at both skip call sites**

In the `LaunchedEffect(currentTargetId)` block (around lines 307-329), replace the two `controller.skipCurrentIfNotVisible()` calls:

First call site (scroll failed, around line 322):
```kotlin
if (!controller.isTargetVisible(currentTargetId)) {
    val shouldRetry = config.retrySkippedTargets && currentTargetId !in retriedTargets
    if (shouldRetry) retriedTargets.add(currentTargetId)
    controller.skipCurrentIfNotVisible(retry = shouldRetry)
}
```

Second call site (no scroller, around line 326):
```kotlin
} else {
    // No scroller provided — skip this step
    val shouldRetry = config.retrySkippedTargets && currentTargetId !in retriedTargets
    if (shouldRetry) retriedTargets.add(currentTargetId)
    controller.skipCurrentIfNotVisible(retry = shouldRetry)
}
```

**Step 3: Build to verify it compiles**

Run: `cd /Users/adit/Documents/AndroidProjects/Lumen && ./gradlew :lumen:compileKotlinMetadata 2>&1 | tail -10`
Expected: BUILD SUCCESSFUL.

**Step 4: Run all existing tests to check for regressions**

Run: `cd /Users/adit/Documents/AndroidProjects/Lumen && ./gradlew :lumen:jvmTest 2>&1 | tail -20`
Expected: All tests PASS.

**Step 5: Commit**

```bash
git add lumen/src/commonMain/kotlin/io/luminos/CoachmarkScrim.kt
git commit -m "feat: wire retrySkippedTargets with infinite loop guard in scrim"
```

---

### Task 4: Device validation

**Step 1: Build and deploy to device**

Run: `cd /Users/adit/Documents/AndroidProjects/Lumen && ./gradlew :sample:installDebug`

**Step 2: Manual test scenario**

1. Open the sample app -> LazyColumn Demo
2. Scroll down so Items 1-5 are off-screen
3. Tap "Start Tour"
4. Advance through steps — off-screen targets should be appended to end
5. When appended steps are reached, `scrollRequester` should auto-scroll to them
6. Verify no (0,0) flash, no infinite loops
7. Verify the step indicator total count stays the same throughout

**Step 3: Verify with `retrySkippedTargets = false` (default)**

Confirm the default behavior is unchanged — off-screen targets are still permanently skipped.
