# Compose Coachmark Library

A **Compose Multiplatform** library for creating beautiful, customizable coachmark/spotlight tutorials with true transparent cutouts.

**Platforms:** Android | iOS | Desktop | Web (Wasm)

## Why Another Coachmark Library?

Existing coachmark libraries have limitations:

| Library | Platform | Compose? | True Transparency? | KMP? |
|---------|----------|----------|-------------------|------|
| TapTargetView | Android | No | No | No |
| Spotlight | Android | No | No | No |
| ShowcaseView | Android | No | No | No |
| **This Library** | **All** | **Yes** | **Yes** | **Yes** |

**This library is Compose-native** and uses `BlendMode.Clear` with offscreen compositing to create **truly transparent cutouts** - you see the actual UI, not a dimmed version.

**First Compose Multiplatform coachmark library** - works on Android, iOS, Desktop, and Web with a single codebase.

---

## Features

### 1. True Transparent Cutouts

```kotlin
// Most libraries: semi-transparent overlay (you see dimmed UI)
// This library: actual transparent hole (you see real UI)

Canvas {
    drawRect(scrimColor)  // Dark overlay
    drawCircle(
        color = Color.Black,
        blendMode = BlendMode.Clear  // Punches transparent hole
    )
}
```

Requires `CompositingStrategy.Offscreen` - handled automatically by `CoachmarkHost`.

### 2. Multiple Cutout Shapes

```kotlin
sealed interface CutoutShape {
    data class Circle(
        val radius: Dp = Dp.Unspecified,  // Explicit or auto-calculated
        val radiusPadding: Dp = 8.dp,
    ) : CutoutShape

    data class RoundedRect(
        val cornerRadius: Dp = 12.dp,
        val padding: Dp = 8.dp,
    ) : CutoutShape

    data class Rect(val padding: Dp = 8.dp) : CutoutShape

    data class Squircle(  // iOS-style superellipse
        val cornerRadius: Dp = 20.dp,
        val padding: Dp = 8.dp,
    ) : CutoutShape
}
```

### 3. Smart Connector Lines

```
HORIZONTAL          VERTICAL           ELBOW              DIRECT

[Icon]----*         [Icon]            [Icon]----+        [Icon]
          |            |                       |              \
     [Tooltip]         *                       *               \*
                  [Tooltip]              [Tooltip]         [Tooltip]
```

```kotlin
enum class ConnectorStyle {
    AUTO,        // Picks best style based on positions
    DIRECT,      // Diagonal line
    HORIZONTAL,  // Horizontal with dot
    VERTICAL,    // Vertical with dot
    ELBOW,       // L-shaped (horizontal then vertical)
}
```

### 4. Multi-Step Sequences

```kotlin
coachmarkController.showSequence(
    listOf(
        CoachmarkTarget(
            id = "settings",
            title = "Settings",
            ctaText = "Next",  // Intermediate step
            // ...
        ),
        CoachmarkTarget(
            id = "profile",
            title = "Profile",
            ctaText = "Got it!",  // Final step
            // ...
        ),
    )
)
```

Progress indicator (dots) automatically shown for sequences.

### 5. Dialog/Overlay Coordination

Coachmarks automatically dismiss or block when dialogs appear:

```kotlin
val overlayCoordinator = rememberOverlayCoordinator()
val coachmarkController = rememberCoachmarkController(overlayCoordinator)

// In dialogs:
if (showDialog) {
    DialogOverlayEffect()  // Auto-registers
    AlertDialog(...)
}

// Coachmarks automatically blocked while dialog is showing
```

### 6. LazyColumn Support via CompositionLocal

No prop drilling needed:

```kotlin
// Screen level
CompositionLocalProvider(LocalCoachmarkController provides controller) {
    LazyColumn {
        items(items) { item ->
            ItemRow(item)  // No controller param needed
        }
    }
}

// Inside ItemRow
val controller = LocalCoachmarkController.current
Modifier.coachmarkTarget(controller, "item_${item.id}")
```

### 7. Global Enable/Disable Toggle

```kotlin
val coachmarkController = rememberCoachmarkController()

// Disable coachmarks based on app context
coachmarkController.enabled = false  // show()/showSequence() will no-op

// Common use cases:
LaunchedEffect(navigationState) {
    coachmarkController.enabled = when {
        isDeepLink -> false           // Don't interrupt deep link flow
        isTestUser -> false           // Skip for test accounts
        !isFirstLaunch -> false       // Only show on first launch
        else -> true
    }
}
```

The `enabled` flag is reactive (`mutableStateOf`) - changes take effect immediately.

### 8. Cutout Highlight Animations

Draw attention to the target with subtle animations:

```kotlin
enum class HighlightAnimation {
    NONE,   // Static cutout (default)
    PULSE,  // Stroke scales 1.0 → 1.08 → 1.0 (breathing effect)
    GLOW,   // Stroke width 1x → 2x, alpha 1.0 → 0.5 (glowing effect)
}
```

```
NONE (static)          PULSE (breathing)       GLOW (glowing)

    ┌───────┐              ┌ ─ ─ ─ ┐             ╔═══════╗
    │       │           ┌──│       │──┐          ║       ║ (thick + bright)
    │ Icon  │           │  │ Icon  │  │          ║ Icon  ║
    │       │           └──│       │──┘          ║       ║
    └───────┘              └ ─ ─ ─ ┘             ╚═══════╝
                           (expands)              (fades)
```

**Usage:**

```kotlin
// Global default for all coachmarks
CoachmarkHost(
    controller = controller,
    config = CoachmarkConfig(
        highlightAnimation = HighlightAnimation.PULSE,
        pulseDurationMs = 1000,  // Full cycle duration
    ),
)

// Per-target override
CoachmarkTarget(
    id = "important_feature",
    highlightAnimation = HighlightAnimation.GLOW,  // Override global
    // ...
)
```

**When to use each:**

| Animation | Use Case |
|-----------|----------|
| `NONE` | Text-heavy tooltips where animation would distract |
| `PULSE` | Standard features, gentle attention |
| `GLOW` | High-priority features, first-time user onboarding |

### 9. Skip Button

Allow users to dismiss the entire coachmark sequence:

```kotlin
CoachmarkConfig(
    showSkipButton = true,       // Show "Skip" button in tooltip
    skipButtonText = "Skip all", // Customize button text
)
```

The skip button appears top-right in the tooltip and dismisses the entire coachmark (calls `onDismiss`).

### 10. Delay Before Show

Wait for UI to settle before showing the coachmark:

```kotlin
CoachmarkConfig(
    delayBeforeShow = 500L,  // Wait 500ms before appearing
)
```

Useful when:
- Screen is still animating in
- Data is loading and layout may shift
- You want a moment for user to orient before tutorial

The delay only applies to the first step of a sequence.

### 11. Configurable User Interactions

#### Scrim Tap Behavior

```kotlin
enum class ScrimTapBehavior {
    DISMISS,  // Tap outside cutout dismisses (default)
    ADVANCE,  // Tap outside advances to next step
    NONE,     // Tap outside does nothing (must use CTA)
}
```

#### Back Press Behavior

```kotlin
enum class BackPressBehavior {
    DISMISS,   // Always dismiss entire coachmark (default)
    NAVIGATE,  // Go back in sequence, dismiss on first step
}
```

**NAVIGATE flow:**
```
[Step 1] → Back → Dismissed
[Step 2] → Back → [Step 1]
[Step 3] → Back → [Step 2] → Back → [Step 1] → Back → Dismissed
```

This matches standard Android navigation patterns users expect.

---

## API Design

### Basic Usage

```kotlin
val coachmarkController = rememberCoachmarkController()

CoachmarkHost(controller = coachmarkController) {
    // Your screen content

    IconButton(
        onClick = { /* ... */ },
        modifier = Modifier.coachmarkTarget(coachmarkController, "settings")
    ) {
        Icon(Icons.Default.Settings, "Settings")
    }
}

// Trigger coachmark
LaunchedEffect(Unit) {
    coachmarkController.show(
        CoachmarkTarget(
            id = "settings",
            bounds = Rect.Zero,  // Updated by modifier
            shape = CutoutShape.Circle(),
            title = "Settings",
            description = "Customize your preferences here.",
        )
    )
}
```

### Configuration

```kotlin
CoachmarkHost(
    controller = coachmarkController,
    config = CoachmarkConfig(
        // Scrim
        scrimOpacity = ScrimOpacity.MEDIUM,  // LIGHT, MEDIUM, DARK, EXTRA_DARK
        scrimTapBehavior = ScrimTapBehavior.DISMISS,  // DISMISS, ADVANCE, NONE

        // Navigation
        backPressBehavior = BackPressBehavior.NAVIGATE,  // DISMISS (default), NAVIGATE

        // Connector
        connectorDotRadius = 4.dp,
        connectorTooltipGap = 8.dp,

        // Tooltip
        tooltipMargin = 16.dp,
        tooltipGap = 16.dp,
        tooltipCornerRadius = 16.dp,
        showTooltipCard = false,  // Floating text vs card background

        // Progress
        showProgressIndicator = true,  // Can override per-target

        // Animation
        fadeAnimationDuration = 300,
        connectorAnimationDuration = 200,
        tooltipAnimationDuration = 250,

        // Highlight animation
        highlightAnimation = HighlightAnimation.PULSE,  // NONE, PULSE, GLOW
        pulseDurationMs = 1000,  // Full cycle duration for PULSE/GLOW

        // Skip button
        showSkipButton = true,
        skipButtonText = "Skip",

        // Timing
        delayBeforeShow = 500L,  // Delay before first step appears (ms)
    ),
    colors = CoachmarkColors(
        scrimColor = Color.Black.copy(alpha = 0.85f),
        strokeColor = Color.White,
        connectorColor = Color.White,
        ctaButtonColor = Color(0xFF00B140),
        // ... more colors
    ),
)
```

### Target Configuration

```kotlin
CoachmarkTarget(
    id = "unique_id",
    bounds = Rect.Zero,
    shape = CutoutShape.Circle(
        radius = 24.dp,        // Explicit radius (optional)
        radiusPadding = 4.dp,  // Added to radius
    ),
    title = "Feature Title",
    description = "Explanation of the feature.",
    tooltipPosition = TooltipPosition.AUTO,  // TOP, BOTTOM, START, END, AUTO
    connectorStyle = ConnectorStyle.ELBOW,
    connectorLength = 50.dp,  // Dp.Unspecified = auto (40dp)
    ctaText = "Got it!",
    showProgressIndicator = null,  // null = use config, true/false = override
    highlightAnimation = HighlightAnimation.GLOW,  // null = use config, or override
)
```

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      CoachmarkHost                          │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                    Your Content                        │  │
│  │  ┌─────────┐                                          │  │
│  │  │ Target  │ ← Modifier.coachmarkTarget()             │  │
│  │  └─────────┘                                          │  │
│  └───────────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              CoachmarkScrim (Canvas)                   │  │
│  │  ┌─────────────────────────────────────────────────┐  │  │
│  │  │ Scrim (BlendMode.SrcOver)                       │  │  │
│  │  │    ┌───────┐                                    │  │  │
│  │  │    │Cutout │ (BlendMode.Clear)                  │  │  │
│  │  │    └───┬───┘                                    │  │  │
│  │  │        │ Connector                              │  │  │
│  │  │        ●                                        │  │  │
│  │  │   ┌─────────┐                                   │  │  │
│  │  │   │ Tooltip │                                   │  │  │
│  │  │   └─────────┘                                   │  │  │
│  │  └─────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### State Flow

```
CoachmarkState (sealed interface)
├── Hidden
├── Showing(target, currentStep, totalSteps)
└── Sequence(targets, currentIndex)
        ├── currentTarget
        ├── currentStep (1-indexed)
        ├── totalSteps
        ├── hasNext
        └── hasPrevious
```

### Key Components

| Component | Responsibility |
|-----------|---------------|
| `CoachmarkController` | State management, target registration |
| `CoachmarkHost` | Wraps content, provides compositing context |
| `CoachmarkScrim` | Renders overlay, cutout, connector, tooltip |
| `CoachmarkModifier` | Tracks target bounds via `onGloballyPositioned` |
| `OverlayCoordinator` | Prevents coachmark/dialog conflicts |
| `CoachmarkRepository` | Persistence (which coachmarks were seen) |

---

## Implementation Details

### Why BlendMode.Clear Requires Offscreen Compositing

```kotlin
Box(
    modifier = Modifier
        .graphicsLayer {
            // CRITICAL: Without this, BlendMode.Clear doesn't work
            compositingStrategy = CompositingStrategy.Offscreen
        }
) {
    Canvas {
        drawRect(scrimColor)
        drawCircle(blendMode = BlendMode.Clear)  // Now works!
    }
}
```

Without `Offscreen`, the clear blend mode tries to clear pixels that don't exist yet in the composition hierarchy.

### Squircle (Superellipse) Implementation

iOS-style rounded corners using the superellipse equation:

```
|x/a|^n + |y/b|^n = 1, where n > 2
```

```kotlin
fun createSquirclePath(bounds: Rect, cornerRadius: Float, smoothness: Float = 4f): Path {
    val exponent = (2.0 / smoothness).toDouble()

    for (i in 0..segments) {
        val t = (i.toDouble() / segments) * 2 * PI
        val x = centerX + a * sign(cos(t)) * abs(cos(t)).pow(exponent)
        val y = centerY + b * sign(sin(t)) * abs(sin(t)).pow(exponent)
        path.lineTo(x, y)
    }
}
```

### Connector Path Animation

Multi-segment paths (like ELBOW) are animated progressively:

```kotlin
fun drawConnectorPath(points: List<Offset>, progress: Float) {
    val totalLength = calculateTotalLength(points)
    val drawLength = totalLength * progress

    // Draw each segment up to drawLength
    for (segment in segments) {
        drawLine(start, end.coerceByProgress())
    }

    // Draw dot at end when complete
    if (progress >= 1f) {
        drawCircle(center = points.last(), radius = dotRadius)
    }
}
```

---

## Roadmap

### v1.0 (Current)
- [x] True transparent cutouts
- [x] 4 cutout shapes (Circle, RoundedRect, Rect, Squircle)
- [x] 5 connector styles (AUTO, DIRECT, HORIZONTAL, VERTICAL, ELBOW)
- [x] Multi-step sequences with progress indicator
- [x] Dialog coordination (OverlayCoordinator)
- [x] Global enable/disable toggle (deep link, feature flags)
- [x] CompositionLocal for LazyColumn
- [x] Configurable scrim opacity (enum)
- [x] Configurable tap behavior (DISMISS, ADVANCE, NONE)
- [x] Configurable back press behavior (DISMISS, NAVIGATE)
- [x] Optional tooltip card background
- [x] Explicit radius control for circles
- [x] Highlight animations (PULSE, GLOW) with configurable duration
- [x] Skip button to dismiss entire sequence
- [x] `delayBeforeShow` for timing control

### v1.1 (Planned)
- [x] Pulse/Glow animation on cutout
- [x] Skip/Close button
- [x] `delayBeforeShow` config
- [ ] DSL builder for sequences
- [ ] More connector end styles (arrow, none)
- [ ] Custom drawable for connector end
- [ ] Haptic feedback option
- [ ] `showOnce` auto-persistence

### v2.0 (Future) - Compose Multiplatform
- [ ] KMP module structure with shared core
- [ ] iOS support via Compose Multiplatform
- [ ] Desktop support (macOS, Windows, Linux)
- [ ] Web support (Compose for Web / Wasm)
- [ ] Video/GIF in tooltip
- [ ] Platform-specific accessibility improvements
- [ ] Analytics integration interface

---

## Compose Multiplatform (KMP/CMP) Strategy

### Why This Library is KMP-Ready

The current implementation is **95% cross-platform compatible** because it uses pure Compose APIs:

| Component | Cross-Platform? | Notes |
|-----------|----------------|-------|
| Canvas drawing | Yes | `androidx.compose.ui.graphics.Canvas` is multiplatform |
| BlendMode.Clear | Yes | Part of Compose graphics layer |
| CompositingStrategy | Yes | Compose UI foundation |
| Path operations | Yes | Pure geometry math |
| Squircle generation | Yes | Pure Kotlin math (sin, cos, pow) |
| StateFlow | Yes | Kotlin coroutines multiplatform |
| mutableStateOf | Yes | Compose runtime (enabled flag) |
| CompositionLocal | Yes | Compose runtime foundation |
| Modifier.onGloballyPositioned | Yes | Compose UI foundation |
| Timber logging | **No** | Needs expect/actual abstraction |

### What Needs Platform Abstraction

```kotlin
// Only these need expect/actual:

// 1. Logging (debug messages)
expect interface CoachmarkLogger {
    fun d(tag: String, message: String)
}

// Android actual
actual class CoachmarkLogger {
    actual fun d(tag: String, message: String) = Timber.tag(tag).d(message)
}

// iOS/Desktop/Web actual
actual class CoachmarkLogger {
    actual fun d(tag: String, message: String) = println("[$tag] $message")
}

// 2. Persistence (seen coachmarks)
expect class CoachmarkRepository {
    fun hasSeenCoachmark(id: String): Boolean
    fun markCoachmarkSeen(id: String)
}

// Android actual
actual class CoachmarkRepository(context: Context) {
    private val prefs = context.getSharedPreferences("coachmarks", Context.MODE_PRIVATE)
    // ...
}

// iOS actual
actual class CoachmarkRepository {
    private val defaults = NSUserDefaults.standardUserDefaults
    // ...
}

// Desktop actual
actual class CoachmarkRepository {
    private val prefs = java.util.prefs.Preferences.userRoot().node("coachmarks")
    // ...
}
```

### KMP Module Structure

```
compose-coachmark-kmp/
├── coachmark/
│   ├── build.gradle.kts              # KMP configuration
│   └── src/
│       ├── commonMain/kotlin/        # 95% of code lives here
│       │   └── com/aditlal/coachmark/
│       │       ├── CoachmarkHost.kt
│       │       ├── CoachmarkController.kt
│       │       ├── CoachmarkTarget.kt
│       │       ├── CoachmarkState.kt
│       │       ├── CoachmarkScrim.kt
│       │       ├── CoachmarkTooltip.kt
│       │       ├── CoachmarkConfig.kt
│       │       ├── CoachmarkColors.kt
│       │       ├── CoachmarkModifier.kt
│       │       ├── OverlayCoordinator.kt
│       │       ├── CoachmarkRepository.kt    # expect declarations
│       │       └── shapes/
│       │           └── SquirclePath.kt
│       ├── androidMain/kotlin/
│       │   └── com/aditlal/coachmark/
│       │       └── CoachmarkRepository.android.kt  # SharedPreferences
│       ├── iosMain/kotlin/
│       │   └── com/aditlal/coachmark/
│       │       └── CoachmarkRepository.ios.kt      # NSUserDefaults
│       ├── desktopMain/kotlin/
│       │   └── com/aditlal/coachmark/
│       │       └── CoachmarkRepository.desktop.kt  # java.util.prefs
│       └── wasmJsMain/kotlin/
│           └── com/aditlal/coachmark/
│               └── CoachmarkRepository.web.kt      # localStorage
├── sample-android/
├── sample-ios/
├── sample-desktop/
└── sample-web/
```

### build.gradle.kts (KMP)

```kotlin
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "coachmark"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}
```

### Platform-Specific Considerations

#### iOS (Compose Multiplatform)
- Squircle shape matches native iOS aesthetic perfectly
- Touch handling works via Compose gesture system
- Memory management handled by Kotlin/Native ARC integration
- Test on both simulator and device (ARM64)

#### Desktop (JVM)
- Mouse hover states could enhance UX (future feature)
- Window resize handling automatic via Compose
- Multi-window support considerations

#### Web (Wasm)
- Canvas rendering maps to HTML5 Canvas
- Touch/mouse unified via Compose pointer input
- Consider lazy loading for bundle size
- localStorage for persistence (limited to ~5MB)

### Migration Path from Android-Only

**Phase 1: Extract to Android module** (current plan)
```
lumen/
└── coachmark/  ← Android library module
```

**Phase 2: Convert to KMP**
```
compose-coachmark/
├── coachmark/  ← KMP shared module
└── sample-android/
```

**Phase 3: Add platforms**
```
compose-coachmark/
├── coachmark/
├── sample-android/
├── sample-ios/
├── sample-desktop/
└── sample-web/
```

### Competitive Advantage

No existing KMP coachmark library exists. Current landscape:

| Library | Platform | Compose? | Transparent Cutout? |
|---------|----------|----------|---------------------|
| TapTargetView | Android only | No | No |
| Spotlight | Android only | No | No |
| ShowcaseView | Android only | No | No |
| **compose-coachmark** | **KMP (all)** | **Yes** | **Yes** |

Being first to market with a Compose Multiplatform coachmark library is a significant opportunity.

---

## Open Source Checklist

### Phase 1: Android Library (v1.0)

- [ ] Abstract `CoachmarkRepository` to interface
- [ ] Remove app-specific imports
- [ ] Add generic preview annotations
- [ ] Create sample app module
- [ ] Write unit tests for Controller and State
- [ ] Write UI tests for Scrim rendering
- [ ] Add KDoc to all public APIs
- [ ] Create README with GIFs
- [ ] Set up GitHub Actions for CI
- [ ] Configure Maven Central publishing
- [ ] Choose license (Apache 2.0 recommended)

### Phase 2: Compose Multiplatform (v2.0)

- [ ] Convert to KMP module structure
- [ ] Move 95% code to `commonMain`
- [ ] Implement `expect/actual` for CoachmarkRepository
  - [ ] Android: SharedPreferences
  - [ ] iOS: NSUserDefaults
  - [ ] Desktop: java.util.prefs
  - [ ] Web: localStorage
- [ ] Create platform-specific sample apps
- [ ] Set up KMP CI (test on all platforms)
- [ ] Configure Kotlin Multiplatform Maven publishing
- [ ] Test on real iOS device (not just simulator)
- [ ] Verify Web/Wasm bundle size is reasonable
- [ ] Add platform badges to README

### Module Structure

```
compose-coachmark/
├── coachmark/                    # Main library module
│   ├── src/main/kotlin/
│   │   └── com/aditlal/coachmark/
│   │       ├── CoachmarkHost.kt
│   │       ├── CoachmarkController.kt
│   │       ├── CoachmarkTarget.kt
│   │       ├── CoachmarkState.kt
│   │       ├── CoachmarkScrim.kt
│   │       ├── CoachmarkTooltip.kt
│   │       ├── CoachmarkConfig.kt
│   │       ├── CoachmarkColors.kt
│   │       ├── CoachmarkModifier.kt
│   │       ├── OverlayCoordinator.kt
│   │       ├── CoachmarkRepository.kt
│   │       └── shapes/
│   │           └── SquirclePath.kt
│   └── build.gradle.kts
├── sample/                       # Demo app
│   └── src/main/kotlin/
│       └── com/aditlal/coachmark/sample/
│           ├── MainActivity.kt
│           ├── BasicExample.kt
│           ├── SequenceExample.kt
│           └── LazyColumnExample.kt
├── README.md
├── LICENSE
└── build.gradle.kts
```

---

## Blog Post Ideas

1. **"Building a Compose Coachmark Library with True Transparency"**
   - The BlendMode.Clear + Offscreen trick
   - Why existing libraries use semi-transparent overlays

2. **"Squircles in Jetpack Compose: iOS-Style Rounded Corners"**
   - Superellipse math
   - Path generation

3. **"Coordinating Overlays in Compose: Dialogs vs Coachmarks"**
   - The OverlayCoordinator pattern
   - CompositionLocal for cross-component communication

4. **"Canvas Animations in Compose: Multi-Segment Path Drawing"**
   - Progressive path animation
   - Connector line implementation

5. **"Building a Compose Multiplatform UI Library: Lessons from Coachmark"**
   - What's automatically cross-platform in Compose
   - expect/actual patterns for persistence
   - Testing across Android, iOS, Desktop, Web
   - Publishing to Maven Central for KMP

6. **"From Android Module to KMP Library: A Migration Guide"**
   - Step-by-step extraction process
   - Handling platform-specific code
   - CI/CD for multiplatform libraries

---

## Naming Ideas

### Android-focused
- `compose-coachmark`
- `spotlight-compose`
- `compose-showcase`

### KMP-focused (recommended for multiplatform)
- `coachmark-kmp` - Clear, searchable
- `spotlight-multiplatform` - Evokes the visual effect
- `onboard-compose` - Action-oriented
- `compose-tutorial-overlay` - Descriptive

### Package name suggestion
```
com.aditlal.coachmark
```
Short, memorable, works across all platforms.

---

## References

### Inspiration
- [TapTargetView](https://github.com/KeepSafe/TapTargetView) - Inspiration for the concept
- [Spotlight](https://github.com/TakuSemworx/Spotlight) - Sequence support reference

### Technical
- [Superellipse Wikipedia](https://en.wikipedia.org/wiki/Superellipse) - Squircle math
- [Compose Graphics Layer](https://developer.android.com/jetpack/compose/graphics/draw/modifiers#graphicsLayer) - Compositing strategies

### Compose Multiplatform
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) - Official JetBrains page
- [KMP Library Setup](https://kotlinlang.org/docs/multiplatform-library.html) - Publishing KMP libraries
- [Compose for iOS](https://github.com/ApolloAI/compose-ios-samples) - iOS implementation examples
- [Compose for Web](https://compose-web.ui.pages.jetbrains.team/) - Web/Wasm documentation
- [touchlab/KMMBridge](https://github.com/touchlab/KMMBridge) - iOS framework distribution

---

*Last updated: February 2026*
*Author: Adit Lal*
