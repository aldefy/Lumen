# Tooltip Text Alignment & Inline Title Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add `tooltipTextAlign` parameter to center-align tooltip text, and `titleInlineWithConnector` to render the title beside the connector dot on the same line.

**Architecture:** Two additive features on the existing coachmark tooltip system. Feature 1 (textAlign) adds a `TextAlign` parameter threaded from config/target through to `CoachmarkTooltip`. Feature 2 (inline title) restructures the tooltip layout to optionally place the title in a `Row` beside a composed dot indicator, suppressing the canvas-drawn dot.

**Tech Stack:** Kotlin Multiplatform, Jetpack Compose, Roborazzi screenshot tests

---

### Task 1: Add `tooltipTextAlign` to CoachmarkConfig and CoachmarkTarget

**Files:**
- Modify: `lumen/src/commonMain/kotlin/io/luminos/CoachmarkTarget.kt:237-254` (CoachmarkTarget data class)
- Modify: `lumen/src/commonMain/kotlin/io/luminos/CoachmarkScrim.kt:167-231` (CoachmarkConfig data class)

**Step 1: Write the failing test**

Add to `lumen/src/commonTest/kotlin/io/luminos/CoachmarkTargetTest.kt`:

```kotlin
import androidx.compose.ui.text.style.TextAlign

@Test
fun default_tooltipTextAlign_is_null() {
    val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
    assertNull(target.tooltipTextAlign)
}

@Test
fun custom_tooltipTextAlign() {
    val target = CoachmarkTarget(id = "t1", title = "T", description = "D", tooltipTextAlign = TextAlign.Center)
    assertEquals(TextAlign.Center, target.tooltipTextAlign)
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew :lumen:jvmTest --tests "io.luminos.CoachmarkTargetTest.default_tooltipTextAlign_is_null" --tests "io.luminos.CoachmarkTargetTest.custom_tooltipTextAlign"`
Expected: FAIL — `tooltipTextAlign` property doesn't exist yet

**Step 3: Add `tooltipTextAlign` to both data classes**

In `CoachmarkTarget.kt` at line 253 (before closing paren), add:

```kotlin
val tooltipTextAlign: TextAlign? = null,
```

Add import at top:
```kotlin
import androidx.compose.ui.text.style.TextAlign
```

In `CoachmarkScrim.kt` inside `CoachmarkConfig`, after `ctaCornerRadius` (line 230), add:

```kotlin
/** Text alignment for tooltip title, description, and other text elements.
 *  Per-target override via [CoachmarkTarget.tooltipTextAlign]. */
val tooltipTextAlign: TextAlign = TextAlign.Start,
```

Add import at top:
```kotlin
import androidx.compose.ui.text.style.TextAlign
```

**Step 4: Run test to verify it passes**

Run: `./gradlew :lumen:jvmTest --tests "io.luminos.CoachmarkTargetTest"`
Expected: PASS

**Step 5: Commit**

```
feat(tooltip): add tooltipTextAlign to CoachmarkConfig and CoachmarkTarget
```

---

### Task 2: Thread `tooltipTextAlign` through to CoachmarkTooltip

**Files:**
- Modify: `lumen/src/commonMain/kotlin/io/luminos/CoachmarkTooltip.kt:66-87` (function signature)
- Modify: `lumen/src/commonMain/kotlin/io/luminos/CoachmarkScrim.kt:861-951` (TooltipContainer)

**Step 1: Add `textAlign` parameter to `CoachmarkTooltip`**

In `CoachmarkTooltip.kt`, add param after `modifier`:

```kotlin
fun CoachmarkTooltip(
    // ... existing params ...
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
```

Add import:
```kotlin
import androidx.compose.ui.text.style.TextAlign
```

**Step 2: Apply `textAlign` to all Text composables**

Title text (line 149-157) — add `textAlign` param and `fillMaxWidth()` modifier:

```kotlin
Text(
    text = title,
    color = titleTextColor,
    fontSize = 20.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 26.sp,
    textAlign = textAlign,
    style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
    modifier = Modifier.fillMaxWidth().semantics { heading() },
)
```

Description text (line 162-168) — add `textAlign` and `fillMaxWidth()`:

```kotlin
Text(
    text = description,
    color = descriptionTextColor,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    textAlign = textAlign,
    style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
    modifier = Modifier.fillMaxWidth(),
)
```

Skip button text (line 137-143) — add `textAlign`:

```kotlin
Text(
    text = skipButtonText,
    color = if (showCard) colors.descriptionColor else colors.strokeColor.copy(alpha = 0.7f),
    fontSize = 14.sp,
    fontWeight = FontWeight.Medium,
    textAlign = textAlign,
    style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
)
```

"Don't show again" text (line 242-257) — add `textAlign`:

```kotlin
Text(
    text = dontShowAgainText,
    color = if (showCard) colors.descriptionColor else colors.strokeColor.copy(alpha = 0.8f),
    fontSize = 13.sp,
    textAlign = textAlign,
    style = if (!showCard) { ... } else { TextStyle.Default },
)
```

**Step 3: Resolve and pass through in CoachmarkScrim**

In `CoachmarkScrim.kt`, before the `TooltipContainer` call (~line 861), resolve the text align:

```kotlin
val resolvedTextAlign = target.tooltipTextAlign ?: config.tooltipTextAlign
```

Add `textAlign = resolvedTextAlign` to the `TooltipContainer` call.

In `TooltipContainer` function signature (~line 900), add parameter:

```kotlin
textAlign: TextAlign = TextAlign.Start,
```

Pass it through to `CoachmarkTooltip` call inside `TooltipContainer`:

```kotlin
CoachmarkTooltip(
    // ... existing params ...
    textAlign = textAlign,
)
```

**Step 4: Run all tests**

Run: `./gradlew :lumen:jvmTest`
Expected: PASS (existing tests unaffected since default is `TextAlign.Start`)

**Step 5: Commit**

```
feat(tooltip): thread tooltipTextAlign through to all tooltip text elements
```

---

### Task 3: Add `titleInlineWithConnector` to CoachmarkConfig and CoachmarkTarget

**Files:**
- Modify: `lumen/src/commonMain/kotlin/io/luminos/CoachmarkTarget.kt`
- Modify: `lumen/src/commonMain/kotlin/io/luminos/CoachmarkScrim.kt` (CoachmarkConfig)

**Step 1: Write the failing test**

Add to `CoachmarkTargetTest.kt`:

```kotlin
@Test
fun default_titleInlineWithConnector_is_null() {
    val target = CoachmarkTarget(id = "t1", title = "T", description = "D")
    assertNull(target.titleInlineWithConnector)
}

@Test
fun custom_titleInlineWithConnector() {
    val target = CoachmarkTarget(id = "t1", title = "T", description = "D", titleInlineWithConnector = true)
    assertEquals(true, target.titleInlineWithConnector)
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew :lumen:jvmTest --tests "io.luminos.CoachmarkTargetTest.default_titleInlineWithConnector_is_null"`
Expected: FAIL

**Step 3: Add the properties**

In `CoachmarkTarget`:

```kotlin
val titleInlineWithConnector: Boolean? = null,
```

In `CoachmarkConfig`, after `tooltipTextAlign`:

```kotlin
/** When true and connector is vertical, renders the title beside the connector dot
 *  on the same horizontal line. Per-target override via [CoachmarkTarget.titleInlineWithConnector]. */
val titleInlineWithConnector: Boolean = false,
```

**Step 4: Run test to verify it passes**

Run: `./gradlew :lumen:jvmTest --tests "io.luminos.CoachmarkTargetTest"`
Expected: PASS

**Step 5: Commit**

```
feat(tooltip): add titleInlineWithConnector to CoachmarkConfig and CoachmarkTarget
```

---

### Task 4: Refactor CoachmarkTooltip to support inline title mode

**Files:**
- Modify: `lumen/src/commonMain/kotlin/io/luminos/CoachmarkTooltip.kt`

**Step 1: Add `titleInlineWithConnector` and `connectorDotColor` params to `CoachmarkTooltip`**

```kotlin
fun CoachmarkTooltip(
    // ... existing params ...
    textAlign: TextAlign = TextAlign.Start,
    titleInlineWithConnector: Boolean = false,
    connectorDotColor: Color = Color.White,
    connectorDotRadius: Dp = 4.dp,
) {
```

**Step 2: Implement the inline title layout**

When `titleInlineWithConnector` is true, replace the title `Text` with a `Row` containing a dot and the title. Move the existing title rendering into a conditional:

```kotlin
if (titleInlineWithConnector) {
    // Inline title: dot + title on same line
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Composed dot indicator
        Box(
            modifier = Modifier
                .size(connectorDotRadius * 2)
                .clip(CircleShape)
                .background(connectorDotColor),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = title,
            color = titleTextColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 26.sp,
            textAlign = textAlign,
            style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
            modifier = Modifier.weight(1f).semantics { heading() },
        )
    }
} else {
    // Standard title
    Text(
        text = title,
        color = titleTextColor,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 26.sp,
        textAlign = textAlign,
        style = textShadow?.let { TextStyle(shadow = it) } ?: TextStyle.Default,
        modifier = Modifier.fillMaxWidth().semantics { heading() },
    )
}
```

**Step 3: Run tests**

Run: `./gradlew :lumen:jvmTest`
Expected: PASS (default is false, so existing behavior unchanged)

**Step 4: Commit**

```
feat(tooltip): support inline title layout with connector dot
```

---

### Task 5: Wire inline title through CoachmarkScrim and suppress canvas dot

**Files:**
- Modify: `lumen/src/commonMain/kotlin/io/luminos/CoachmarkScrim.kt`

**Step 1: Resolve `titleInlineWithConnector` and determine if inline is active**

Before the `TooltipContainer` call (~line 861), add:

```kotlin
val resolvedTitleInline = target.titleInlineWithConnector ?: config.titleInlineWithConnector
// Inline title only works with vertical connectors
val resolvedConnectorStyle = /* the same resolved style used for connector path calc */
val isInlineTitleActive = resolvedTitleInline && (resolvedConnectorStyle == ConnectorStyle.VERTICAL)
```

Note: The connector style resolution already happens inside `calculateConnectorPath`. We need to extract or duplicate the AUTO→resolved logic. The simplest approach: add a `resolveConnectorStyle()` helper that both the path calc and the inline check can use.

**Step 2: Add `resolveConnectorStyle` helper**

Extract the existing logic from `calculateConnectorPath` (lines 1412-1426) into a standalone function:

```kotlin
private fun resolveConnectorStyle(
    connectorStyle: ConnectorStyle,
    targetCenter: Offset,
    tooltipCenterX: Float,
    tooltipCenterY: Float,
    cutoutRadius: Float,
): ConnectorStyle {
    if (connectorStyle != ConnectorStyle.AUTO) return connectorStyle
    val horizontalDistance = kotlin.math.abs(tooltipCenterX - targetCenter.x)
    val verticalDistance = kotlin.math.abs(tooltipCenterY - targetCenter.y)
    val hasSignificantHorizontal = horizontalDistance > cutoutRadius * 2
    val hasSignificantVertical = verticalDistance > cutoutRadius * 2
    return when {
        hasSignificantHorizontal && hasSignificantVertical -> ConnectorStyle.ELBOW
        verticalDistance > horizontalDistance -> ConnectorStyle.VERTICAL
        else -> ConnectorStyle.HORIZONTAL
    }
}
```

Use this in `calculateConnectorPath` to replace the inline resolution, and also call it from the scrim composable to determine `isInlineTitleActive`.

**Step 3: Pass to TooltipContainer and suppress canvas dot**

Add params to `TooltipContainer`:

```kotlin
titleInlineWithConnector: Boolean = false,
connectorDotColor: Color = Color.White,
connectorDotRadius: Dp = 4.dp,
```

Pass through to `CoachmarkTooltip`.

For the canvas dot suppression: when `isInlineTitleActive` is true, override the `endStyle` passed to `drawConnectorSegmented`/`drawConnectorCurve` to `ConnectorEndStyle.NONE`:

```kotlin
val effectiveEndStyle = if (isInlineTitleActive) ConnectorEndStyle.NONE else target.connectorEndStyle
```

Use `effectiveEndStyle` in the connector drawing calls (~lines 835, 849).

**Step 4: Pass connector dot styling to TooltipContainer**

```kotlin
TooltipContainer(
    // ... existing params ...
    textAlign = resolvedTextAlign,
    titleInlineWithConnector = isInlineTitleActive,
    connectorDotColor = colors.connectorColor,
    connectorDotRadius = config.connectorDotRadius,
)
```

**Step 5: Run all tests**

Run: `./gradlew :lumen:jvmTest`
Expected: PASS

**Step 6: Commit**

```
feat(tooltip): wire inline title through scrim and suppress canvas dot when active
```

---

### Task 6: Add screenshot tests for new features

**Files:**
- Modify: `lumen/src/androidUnitTest/kotlin/io/luminos/screenshot/CoachmarkTooltipScreenshotTest.kt`

**Step 1: Add center-aligned tooltip screenshot test**

```kotlin
@Test
fun tooltip_centerAligned() {
    composeTestRule.setContent {
        MaterialTheme(colorScheme = lightColorScheme()) {
            Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                CoachmarkTooltip(
                    title = "Muted Call",
                    description = "This call has been muted for you.",
                    ctaText = "Got it!",
                    currentStep = 1,
                    totalSteps = 1,
                    colors = LightCoachmarkColors,
                    textAlign = TextAlign.Center,
                    onCtaClick = {},
                )
            }
        }
    }
    capture("tooltip_centerAligned")
}
```

**Step 2: Add inline title screenshot test**

```kotlin
@Test
fun tooltip_inlineTitle() {
    composeTestRule.setContent {
        MaterialTheme(colorScheme = lightColorScheme()) {
            Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                CoachmarkTooltip(
                    title = "Muted Call",
                    description = "This call has been muted for you.",
                    ctaText = "Got it!",
                    currentStep = 1,
                    totalSteps = 1,
                    colors = LightCoachmarkColors,
                    titleInlineWithConnector = true,
                    connectorDotColor = Color.White,
                    onCtaClick = {},
                )
            }
        }
    }
    capture("tooltip_inlineTitle")
}

@Test
fun tooltip_inlineTitle_centerAligned_card() {
    composeTestRule.setContent {
        MaterialTheme(colorScheme = lightColorScheme()) {
            Box(modifier = Modifier.background(Color(0xFF1A1A2E)).padding(16.dp)) {
                CoachmarkTooltip(
                    title = "Muted Call",
                    description = "This call has been muted for you.",
                    ctaText = "Got it!",
                    currentStep = 1,
                    totalSteps = 3,
                    colors = LightCoachmarkColors,
                    showCard = true,
                    textAlign = TextAlign.Center,
                    titleInlineWithConnector = true,
                    connectorDotColor = LightCoachmarkColors.titleColor,
                    onCtaClick = {},
                )
            }
        }
    }
    capture("tooltip_inlineTitle_centerAligned_card")
}
```

**Step 3: Record baseline screenshots**

Run: `./gradlew :lumen:recordRoborazziDebug`
Expected: New baseline screenshots generated in `lumen/src/androidUnitTest/snapshots/`

**Step 4: Verify screenshots pass**

Run: `./gradlew :lumen:verifyRoborazziDebug`
Expected: PASS

**Step 5: Commit**

```
test(tooltip): add screenshot tests for textAlign and inline title
```

---

### Task 7: Update sample app to demonstrate new features

**Files:**
- Find and modify the sample/demo app to add examples of center-aligned and inline title tooltips

**Step 1: Find sample app**

Look for sample/demo module in the project that demonstrates coachmark usage.

**Step 2: Add a demo target using the new features**

Add a `CoachmarkTarget` with:
```kotlin
CoachmarkTarget(
    id = "inline_demo",
    title = "Muted Call",
    description = "This call has been muted for you.",
    tooltipTextAlign = TextAlign.Center,
    titleInlineWithConnector = true,
    connectorStyle = ConnectorStyle.VERTICAL,
)
```

And configure the `CoachmarkConfig` with:
```kotlin
CoachmarkConfig(
    showTooltipCard = true,
    tooltipTextAlign = TextAlign.Center,
    titleInlineWithConnector = true,
)
```

**Step 3: Build and verify**

Run: `./gradlew :sample:assembleDebug` (or whatever the sample module is named)
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```
docs(sample): add demo for center-aligned and inline title tooltips
```

---

## Execution Order

Tasks 1-6 are sequential. Task 7 is independent (can be done after Task 2 if desired).

## Test Commands Summary

| Command | Purpose |
|---------|---------|
| `./gradlew :lumen:jvmTest` | Run all common + JVM unit tests |
| `./gradlew :lumen:jvmTest --tests "io.luminos.CoachmarkTargetTest"` | Run target tests only |
| `./gradlew :lumen:recordRoborazziDebug` | Record new screenshot baselines |
| `./gradlew :lumen:verifyRoborazziDebug` | Verify screenshots match baselines |
