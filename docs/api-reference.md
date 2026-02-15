# API Reference

## CoachmarkHost

The main entry point for rendering coachmarks. Wraps your content and overlays the scrim.

```kotlin
@Composable
fun CoachmarkHost(
    controller: CoachmarkController,
    modifier: Modifier = Modifier,
    config: CoachmarkConfig = CoachmarkConfig(),
    colors: CoachmarkColors = coachmarkColors(),
    onDismiss: () -> Unit = {},
    onStepCompleted: (stepIndex: Int, targetId: String) -> Unit = { _, _ -> },
    content: @Composable () -> Unit,
)
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `controller` | `CoachmarkController` | Controls coachmark state and operations |
| `modifier` | `Modifier` | Modifier for the host container |
| `config` | `CoachmarkConfig` | Appearance and behavior configuration |
| `colors` | `CoachmarkColors` | Theme colors |
| `onDismiss` | `() -> Unit` | Called when coachmark is dismissed |
| `onStepCompleted` | `(Int, String) -> Unit` | Called when a step is completed (index, targetId) |
| `content` | `@Composable () -> Unit` | Your screen content |

---

## CoachmarkController

Manages coachmark state and provides methods to show/dismiss coachmarks.

### Creating a Controller

```kotlin
val controller = rememberCoachmarkController()

// With dialog coordination
val overlayCoordinator = rememberOverlayCoordinator()
val controller = rememberCoachmarkController(overlayCoordinator)
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `state` | `StateFlow<CoachmarkState>` | Current coachmark state (observable) |
| `enabled` | `Boolean` | Global toggle. When `false`, show/showSequence no-op |
| `isBlockedByDialog` | `Boolean` | `true` if a dialog is currently blocking coachmarks |

### Methods

| Method | Description |
|--------|-------------|
| `show(target: CoachmarkTarget): Boolean` | Show a single coachmark. Returns `false` if blocked |
| `showSequence(targets: List<CoachmarkTarget>): Boolean` | Show multi-step sequence. Returns `false` if blocked |
| `next()` | Advance to next step, or dismiss if on last step |
| `previous()` | Go back one step (no-op on first step) |
| `dismiss()` | Dismiss the coachmark immediately |
| `registerTarget(id: String, bounds: Rect)` | Internal: register target bounds |
| `unregisterTarget(id: String)` | Internal: unregister target |
| `getTargetBounds(id: String): Rect?` | Get bounds for a registered target |

---

## CoachmarkTarget

Defines a single coachmark target with its tooltip content and appearance.

```kotlin
data class CoachmarkTarget(
    val id: String,
    val bounds: Rect = Rect.Zero,
    val shape: CutoutShape = CutoutShape.Circle(),
    val title: String,
    val description: String,
    val tooltipPosition: TooltipPosition = TooltipPosition.AUTO,
    val connectorStyle: ConnectorStyle = ConnectorStyle.AUTO,
    val connectorLength: Dp = Dp.Unspecified,
    val ctaText: String = "Got it!",
    val showProgressIndicator: Boolean? = null,
    val highlightAnimation: HighlightAnimation? = null,
)
```

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `id` | `String` | required | Unique identifier matching `coachmarkTarget()` modifier |
| `bounds` | `Rect` | `Rect.Zero` | Screen coordinates (auto-populated from modifier) |
| `shape` | `CutoutShape` | `Circle()` | Shape of the transparent cutout |
| `title` | `String` | required | Tooltip headline text |
| `description` | `String` | required | Tooltip body text |
| `tooltipPosition` | `TooltipPosition` | `AUTO` | Where tooltip appears relative to target |
| `connectorStyle` | `ConnectorStyle` | `AUTO` | Style of connector line |
| `connectorLength` | `Dp` | `Unspecified` | Connector length (auto: 40dp) |
| `ctaText` | `String` | `"Got it!"` | Call-to-action button text |
| `showProgressIndicator` | `Boolean?` | `null` | Override global progress setting |
| `highlightAnimation` | `HighlightAnimation?` | `null` | Override global animation |

---

## CutoutShape

Sealed interface defining the cutout shape around the target.

### Circle

```kotlin
CutoutShape.Circle(
    radius: Dp = Dp.Unspecified,
    radiusPadding: Dp = 8.dp,
)
```

| Property | Description |
|----------|-------------|
| `radius` | Explicit radius. `Unspecified` = auto-calculated from bounds |
| `radiusPadding` | Additional padding added to radius |

### Rect

```kotlin
CutoutShape.Rect(
    padding: Dp = 8.dp,
)
```

| Property | Description |
|----------|-------------|
| `padding` | Padding around all edges |

### RoundedRect

```kotlin
CutoutShape.RoundedRect(
    cornerRadius: Dp = 12.dp,
    padding: Dp = 8.dp,
)
```

| Property | Description |
|----------|-------------|
| `cornerRadius` | Radius of rounded corners |
| `padding` | Padding around all edges |

### Squircle

```kotlin
CutoutShape.Squircle(
    cornerRadius: Dp = 20.dp,
    padding: Dp = 8.dp,
)
```

| Property | Description |
|----------|-------------|
| `cornerRadius` | Controls the superellipse curve |
| `padding` | Padding around all edges |

### Star

```kotlin
CutoutShape.Star(
    points: Int = 5,
    innerRadiusRatio: Float = 0.5f,
    padding: Dp = 8.dp,
)
```

| Property | Description |
|----------|-------------|
| `points` | Number of star points |
| `innerRadiusRatio` | Ratio of inner to outer radius (0.0-1.0) |
| `padding` | Padding around the star |

---

## CoachmarkConfig

Configuration for coachmark appearance and behavior.

```kotlin
data class CoachmarkConfig(
    val strokeWidth: Dp = 2.dp,
    val connectorDotRadius: Dp = 4.dp,
    val tooltipMargin: Dp = 16.dp,
    val tooltipGap: Dp = 16.dp,
    val tooltipCornerRadius: Dp = 16.dp,
    val connectorTooltipGap: Dp = 8.dp,
    val fadeAnimationDuration: Int = 300,
    val connectorAnimationDuration: Int = 200,
    val tooltipAnimationDuration: Int = 250,
    val scrimOpacity: ScrimOpacity? = null,
    val scrimTapBehavior: ScrimTapBehavior = ScrimTapBehavior.DISMISS,
    val showProgressIndicator: Boolean = true,
    val showTooltipCard: Boolean = false,
    val backPressBehavior: BackPressBehavior = BackPressBehavior.DISMISS,
    val highlightAnimation: HighlightAnimation = HighlightAnimation.NONE,
    val pulseDurationMs: Int = 1000,
    val showSkipButton: Boolean = false,
    val skipButtonText: String = "Skip",
    val delayBeforeShow: Long = 0L,
)
```

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `strokeWidth` | `Dp` | `2.dp` | Cutout border stroke width |
| `connectorDotRadius` | `Dp` | `4.dp` | Radius of connector endpoint dot |
| `tooltipMargin` | `Dp` | `16.dp` | Minimum distance from screen edges |
| `tooltipGap` | `Dp` | `16.dp` | Gap between cutout and tooltip |
| `tooltipCornerRadius` | `Dp` | `16.dp` | Corner radius for tooltip card |
| `connectorTooltipGap` | `Dp` | `8.dp` | Gap between connector end and tooltip |
| `fadeAnimationDuration` | `Int` | `300` | Fade animation duration (ms) |
| `connectorAnimationDuration` | `Int` | `200` | Connector draw animation duration (ms) |
| `tooltipAnimationDuration` | `Int` | `250` | Tooltip slide animation duration (ms) |
| `scrimOpacity` | `ScrimOpacity?` | `null` | Predefined opacity level |
| `scrimTapBehavior` | `ScrimTapBehavior` | `DISMISS` | Action when tapping scrim |
| `showProgressIndicator` | `Boolean` | `true` | Show step dots for sequences |
| `showTooltipCard` | `Boolean` | `false` | Wrap tooltip in card background |
| `backPressBehavior` | `BackPressBehavior` | `DISMISS` | Action on back press |
| `highlightAnimation` | `HighlightAnimation` | `NONE` | Default cutout animation |
| `pulseDurationMs` | `Int` | `1000` | Animation cycle duration (ms) |
| `showSkipButton` | `Boolean` | `false` | Show skip button in tooltip |
| `skipButtonText` | `String` | `"Skip"` | Text for skip button |
| `delayBeforeShow` | `Long` | `0L` | Delay before appearing (ms) |

---

## CoachmarkColors

Theme colors for the coachmark overlay and tooltip.

```kotlin
data class CoachmarkColors(
    val scrimColor: Color,
    val strokeColor: Color,
    val connectorColor: Color,
    val tooltipBackground: Color,
    val tooltipCardColor: Color,
    val titleColor: Color,
    val descriptionColor: Color,
    val ctaButtonColor: Color,
    val ctaTextColor: Color,
    val progressActiveColor: Color,
    val progressInactiveColor: Color,
)
```

| Property | Description |
|----------|-------------|
| `scrimColor` | Background overlay color |
| `strokeColor` | Cutout border stroke color |
| `connectorColor` | Connector line color |
| `tooltipBackground` | Tooltip background (floating mode) |
| `tooltipCardColor` | Tooltip card background (card mode) |
| `titleColor` | Tooltip title text color |
| `descriptionColor` | Tooltip description text color |
| `ctaButtonColor` | CTA button background color |
| `ctaTextColor` | CTA button text color |
| `progressActiveColor` | Active progress dot color |
| `progressInactiveColor` | Inactive progress dot color |

### Preset Colors

```kotlin
LightCoachmarkColors  // Light theme preset
DarkCoachmarkColors   // Dark theme preset

// Auto-select based on system theme:
coachmarkColors(darkTheme: Boolean = isSystemInDarkTheme())
```

---

## Enums

### TooltipPosition

```kotlin
enum class TooltipPosition {
    TOP,     // Above the target
    BOTTOM,  // Below the target
    START,   // Left (or right in RTL)
    END,     // Right (or left in RTL)
    AUTO,    // Auto-select based on available space
}
```

### ConnectorStyle

```kotlin
enum class ConnectorStyle {
    AUTO,        // Auto-select based on tooltip position
    DIRECT,      // Diagonal line
    HORIZONTAL,  // Horizontal line with dot
    VERTICAL,    // Vertical line with dot
    ELBOW,       // L-shaped connector
}
```

### HighlightAnimation

```kotlin
enum class HighlightAnimation {
    NONE,     // Static cutout
    PULSE,    // Breathing scale effect (1.0 → 1.08 → 1.0)
    GLOW,     // Stroke width pulses, alpha fades
    RIPPLE,   // Expanding rings outward
    SHIMMER,  // Highlight sweeps around stroke
    BOUNCE,   // Energetic scale with overshoot
}
```

### ScrimTapBehavior

```kotlin
enum class ScrimTapBehavior {
    DISMISS,  // Tap dismisses coachmark
    ADVANCE,  // Tap advances to next step
    NONE,     // Tap does nothing
}
```

### BackPressBehavior

```kotlin
enum class BackPressBehavior {
    DISMISS,   // Back dismisses entirely
    NAVIGATE,  // Back goes to previous step
}
```

### ScrimOpacity

```kotlin
enum class ScrimOpacity(val alpha: Float) {
    LIGHT(0.30f),
    MEDIUM(0.50f),
    DARK(0.70f),
    EXTRA_DARK(0.85f),
}
```

---

## Modifier Extension

### coachmarkTarget

Tags a composable as a coachmark target.

```kotlin
fun Modifier.coachmarkTarget(
    controller: CoachmarkController,
    id: String,
): Modifier
```

| Parameter | Description |
|-----------|-------------|
| `controller` | The CoachmarkController managing this target |
| `id` | Unique identifier matching `CoachmarkTarget.id` |

---

## CompositionLocals

### LocalCoachmarkController

Access controller without prop drilling:

```kotlin
// Provide
CompositionLocalProvider(LocalCoachmarkController provides controller) {
    // children can access
}

// Consume
val controller = LocalCoachmarkController.current
controller?.let {
    Modifier.coachmarkTarget(it, "id")
}
```

### LocalOverlayCoordinator

Access overlay coordinator for dialog registration:

```kotlin
val coordinator = LocalOverlayCoordinator.current
```

---

## Helper Functions

### rememberCoachmarkController

```kotlin
@Composable
fun rememberCoachmarkController(
    overlayCoordinator: OverlayCoordinator? = LocalOverlayCoordinator.current,
): CoachmarkController
```

### rememberOverlayCoordinator

```kotlin
@Composable
fun rememberOverlayCoordinator(): OverlayCoordinator
```

### DialogOverlayEffect

Registers a dialog with the overlay coordinator:

```kotlin
@Composable
fun DialogOverlayEffect()
```

### coachmarkColors

Returns appropriate colors based on theme:

```kotlin
@Composable
fun coachmarkColors(
    darkTheme: Boolean = isSystemInDarkTheme()
): CoachmarkColors
```

---

## Persistence

### CoachmarkStorage

Interface for coachmark persistence storage. Implement this to provide a custom storage mechanism.

```kotlin
interface CoachmarkStorage {
    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun remove(key: String)
    fun getAllKeys(): Set<String>
}
```

| Method | Description |
|--------|-------------|
| `getBoolean(key, default)` | Read a boolean value, returning `default` if not found |
| `putBoolean(key, value)` | Write a boolean value |
| `remove(key)` | Remove a stored key |
| `getAllKeys()` | Return all stored keys |

#### Platform Implementations

| Platform | Class | Storage Backend |
|----------|-------|-----------------|
| Android | `SharedPrefsCoachmarkStorage(context)` | SharedPreferences |
| iOS | `NSUserDefaultsCoachmarkStorage()` | NSUserDefaults |
| Desktop (JVM) | `PreferencesCoachmarkStorage()` | java.util.prefs.Preferences |
| Web (Wasm) | `LocalStorageCoachmarkStorage()` | Browser localStorage |

### CoachmarkRepository

Repository for persisting coachmark shown state. Tracks which coachmarks have been seen to prevent showing them again.

```kotlin
class CoachmarkRepository(storage: CoachmarkStorage)
```

| Method | Description |
|--------|-------------|
| `hasSeenCoachmark(id: String): Boolean` | Check if a coachmark has been seen |
| `markCoachmarkSeen(id: String)` | Mark a coachmark as seen |
| `resetCoachmark(id: String)` | Reset a coachmark to unseen state |
| `resetAllCoachmarks()` | Reset all coachmarks to unseen state |

#### Convenience Factories

```kotlin
// Android
fun CoachmarkRepository(context: Context): CoachmarkRepository

// iOS
fun CoachmarkRepository(): CoachmarkRepository

// Desktop (JVM)
fun CoachmarkRepository(): CoachmarkRepository

// Web (Wasm)
fun CoachmarkRepository(): CoachmarkRepository
```
