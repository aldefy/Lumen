# Guide

## Basic Usage

The simplest coachmark requires three things:
1. A `CoachmarkController` to manage state
2. A `CoachmarkHost` to render the overlay
3. A tagged target using `Modifier.coachmarkTarget()`

```kotlin
val controller = rememberCoachmarkController()

CoachmarkHost(controller = controller) {
    Icon(
        Icons.Default.Settings,
        contentDescription = "Settings",
        modifier = Modifier.coachmarkTarget(controller, "settings")
    )
}

// Trigger
controller.show(
    CoachmarkTarget(
        id = "settings",
        title = "Settings",
        description = "Configure your preferences here.",
    )
)
```

## Multi-Step Sequences

Create onboarding tours with multiple steps:

```kotlin
controller.showSequence(
    listOf(
        CoachmarkTarget(
            id = "step1",
            title = "First Feature",
            description = "Start your journey here.",
            ctaText = "Next",
        ),
        CoachmarkTarget(
            id = "step2",
            title = "Second Feature",
            description = "Continue to the next step.",
            ctaText = "Next",
        ),
        CoachmarkTarget(
            id = "step3",
            title = "You're Done!",
            description = "You've completed the tour.",
            ctaText = "Got it",
        ),
    )
)
```

### Progress Indicators

Progress dots are shown automatically for sequences. Disable per-target or globally:

```kotlin
// Disable globally
CoachmarkHost(
    controller = controller,
    config = CoachmarkConfig(showProgressIndicator = false)
)

// Disable for specific target
CoachmarkTarget(
    id = "no-progress",
    showProgressIndicator = false,
    // ...
)
```

### Navigation

Users can navigate sequences:

```kotlin
controller.next()       // Go to next step
controller.previous()   // Go back one step
controller.dismiss()    // Exit sequence
```

Configure back button behavior:

```kotlin
CoachmarkConfig(
    backPressBehavior = BackPressBehavior.NAVIGATE  // Back goes to previous step
    // or
    backPressBehavior = BackPressBehavior.DISMISS   // Back dismisses entirely (default)
)
```

## Cutout Shapes

### Circle

Ideal for FABs, icons, and round buttons:

```kotlin
CutoutShape.Circle(
    radius = Dp.Unspecified,  // Auto-calculated from target bounds
    radiusPadding = 8.dp,     // Extra padding around the circle
)
```

### Rectangle

Sharp corners, good for cards and containers:

```kotlin
CutoutShape.Rect(
    padding = 8.dp,  // Padding around all edges
)
```

### Rounded Rectangle

Rectangular with rounded corners:

```kotlin
CutoutShape.RoundedRect(
    cornerRadius = 12.dp,
    padding = 8.dp,
)
```

### Squircle

iOS-style superellipse with smooth curves:

```kotlin
CutoutShape.Squircle(
    cornerRadius = 20.dp,
    padding = 8.dp,
)
```

### Star

Fun shape for gamification and achievements:

```kotlin
CutoutShape.Star(
    points = 5,              // Number of star points
    innerRadiusRatio = 0.5f, // Ratio of inner to outer radius (0.0-1.0)
    padding = 8.dp,
)
```

## Highlight Animations

Draw attention to the cutout with animations:

| Animation | Effect |
|-----------|--------|
| `NONE` | Static cutout, no animation |
| `PULSE` | Gentle breathing/scaling effect |
| `GLOW` | Pulsing stroke width and opacity |
| `RIPPLE` | Expanding rings emanating outward |
| `SHIMMER` | Highlight sweeping around the stroke |
| `BOUNCE` | Energetic scale with overshoot |

```kotlin
// Set globally
CoachmarkConfig(
    highlightAnimation = HighlightAnimation.PULSE,
    pulseDurationMs = 1000,  // Animation cycle duration
)

// Override per-target
CoachmarkTarget(
    id = "important",
    highlightAnimation = HighlightAnimation.GLOW,
    // ...
)
```

## Connector Styles

The connector line links the cutout to the tooltip:

| Style | Description |
|-------|-------------|
| `AUTO` | Automatically picks best style based on position |
| `VERTICAL` | Straight vertical line |
| `HORIZONTAL` | Straight horizontal line |
| `ELBOW` | L-shaped with 90° bend |
| `DIRECT` | Diagonal line pointing directly to tooltip |

```kotlin
CoachmarkTarget(
    id = "target",
    connectorStyle = ConnectorStyle.ELBOW,
    connectorLength = 60.dp,  // Custom length (default: auto)
    // ...
)
```

## Tooltip Position

Control where the tooltip appears relative to the target:

| Position | Description |
|----------|-------------|
| `AUTO` | Automatically picks best position based on available space |
| `TOP` | Above the target |
| `BOTTOM` | Below the target |
| `START` | Left of the target (or right in RTL) |
| `END` | Right of the target (or left in RTL) |

```kotlin
CoachmarkTarget(
    id = "target",
    tooltipPosition = TooltipPosition.BOTTOM,
    // ...
)
```

## Theming

### Using Preset Colors

```kotlin
CoachmarkHost(
    controller = controller,
    colors = coachmarkColors(),  // Auto light/dark theme
)

// Or explicitly:
colors = LightCoachmarkColors
colors = DarkCoachmarkColors
```

### Custom Colors

```kotlin
CoachmarkHost(
    controller = controller,
    colors = CoachmarkColors(
        scrimColor = Color.Black.copy(alpha = 0.85f),
        strokeColor = Color.White,
        connectorColor = Color.White,
        tooltipBackground = Color.White,
        tooltipCardColor = Color(0xFF2A2A2A),
        titleColor = Color.Black,
        descriptionColor = Color(0xFF666666),
        ctaButtonColor = Color(0xFF007AFF),
        ctaTextColor = Color.White,
        progressActiveColor = Color(0xFF007AFF),
        progressInactiveColor = Color(0xFFE0E0E0),
    ),
)
```

### Scrim Opacity

Predefined opacity levels:

```kotlin
CoachmarkConfig(
    scrimOpacity = ScrimOpacity.LIGHT,      // 30%
    scrimOpacity = ScrimOpacity.MEDIUM,     // 50%
    scrimOpacity = ScrimOpacity.DARK,       // 70%
    scrimOpacity = ScrimOpacity.EXTRA_DARK, // 85%
)
```

## Dialog Coordination

Automatically dismiss coachmarks when dialogs appear:

```kotlin
val overlayCoordinator = rememberOverlayCoordinator()
val controller = rememberCoachmarkController(overlayCoordinator)

// In your dialog:
if (showDialog) {
    DialogOverlayEffect()  // Register dialog with coordinator
    AlertDialog(
        onDismissRequest = { showDialog = false },
        // ...
    )
}
```

## Disabling Coachmarks

Temporarily disable all coachmarks:

```kotlin
// Disable (show() and showSequence() will no-op)
controller.enabled = false

// Re-enable
controller.enabled = true
```

Use cases:
- Deep link flows
- Test/debug modes
- User preferences
- Feature flags

```kotlin
LaunchedEffect(isDeepLink) {
    controller.enabled = !isDeepLink
}
```

## Skip Button

Allow users to dismiss the entire sequence:

```kotlin
CoachmarkConfig(
    showSkipButton = true,
    skipButtonText = "Skip tour",
)
```

## Delay Before Show

Wait for UI to settle before showing:

```kotlin
CoachmarkConfig(
    delayBeforeShow = 500L,  // Wait 500ms
)
```

Useful when:
- Screen is animating in
- Data is loading
- Layout may shift

## Persistence

Track which coachmarks have been shown so they only appear once per user.

### Android

```kotlin
val repository = CoachmarkRepository(context)
```

Uses `SharedPreferences` under the hood.

### iOS

```kotlin
val repository = CoachmarkRepository()
```

Uses `NSUserDefaults` under the hood.

### Using the Repository

```kotlin
val repository = CoachmarkRepository(context) // Android
// val repository = CoachmarkRepository()     // iOS

// Check before showing
if (!repository.hasSeenCoachmark("onboarding")) {
    controller.show(onboardingTarget)
}

// Mark as seen after completion
repository.markCoachmarkSeen("onboarding")

// Reset for testing or "replay tutorial"
repository.resetCoachmark("onboarding")
repository.resetAllCoachmarks()
```

### Custom Storage

Implement `CoachmarkStorage` to use your own persistence backend:

```kotlin
class MyCustomStorage : CoachmarkStorage {
    override fun getBoolean(key: String, default: Boolean): Boolean = TODO()
    override fun putBoolean(key: String, value: Boolean) = TODO()
    override fun remove(key: String) = TODO()
    override fun getAllKeys(): Set<String> = TODO()
}

val repository = CoachmarkRepository(MyCustomStorage())
```
