# Lumen

<p align="center">
  <img src="logo.svg" width="128" height="128" alt="Lumen Logo"/>
</p>

<p align="center">
  <strong>Spotlight your UI, not a screenshot of it.</strong>
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/io.github.aldefy/lumen"><img src="https://img.shields.io/maven-central/v/io.github.aldefy/lumen.svg?label=Maven%20Central&logo=apachemaven" alt="Maven Central"/></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.0-blue.svg?logo=kotlin" alt="Kotlin"/></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Jetpack_Compose-1.6-4285F4.svg?logo=jetpackcompose" alt="Compose"/></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg" alt="License"/></a>
</p>

---

A Jetpack Compose coachmark library that creates **true transparent cutouts** in the overlay scrim. Your actual UI remains visible and interactive through the spotlight - animations play, buttons respond, nothing is faked.

## Installation

```kotlin
dependencies {
    implementation("io.github.aldefy:lumen:1.0.0-beta01")
}
```

## Usage

```kotlin
val controller = rememberCoachmarkController()

CoachmarkHost(controller = controller) {
    // Your screen content
    Button(
        onClick = { },
        modifier = Modifier.coachmarkTarget(controller, "my-button")
    ) {
        Text("Click me")
    }
}

// Show the coachmark
controller.show(
    CoachmarkTarget(
        id = "my-button",
        title = "Welcome",
        description = "Tap here to get started.",
    )
)
```

## API Reference

### Core Components

| Component | Description |
|-----------|-------------|
| `CoachmarkHost` | Wraps your content and renders the overlay scrim |
| `CoachmarkController` | Manages coachmark state, show/dismiss operations |
| `CoachmarkTarget` | Defines a single spotlight target with tooltip content |
| `Modifier.coachmarkTarget()` | Tags a composable as a coachmark target |

### CoachmarkTarget Properties

| Property | Type | Description |
|----------|------|-------------|
| `id` | `String` | Unique identifier for the target |
| `title` | `String` | Tooltip headline |
| `description` | `String` | Tooltip body text |
| `shape` | `CutoutShape` | Shape of the transparent cutout |
| `tooltipPosition` | `TooltipPosition` | Where tooltip appears relative to target |
| `connectorStyle` | `ConnectorStyle` | Style of line connecting cutout to tooltip |
| `highlightAnimation` | `HighlightAnimation` | Animation effect on the cutout |
| `ctaText` | `String` | Call-to-action button text |

### CutoutShape

| Shape | Description |
|-------|-------------|
| `Circle` | Circular cutout, ideal for FABs and icons |
| `Rect` | Rectangular cutout with sharp corners |
| `RoundedRect` | Rectangle with rounded corners |
| `Squircle` | iOS-style superellipse with smooth curves |
| `Star` | Star shape for gamification highlights |

### HighlightAnimation

| Animation | Description |
|-----------|-------------|
| `NONE` | Static cutout, no animation |
| `PULSE` | Gentle breathing/scaling effect |
| `GLOW` | Pulsing stroke width and opacity |
| `RIPPLE` | Expanding rings emanating outward |
| `SHIMMER` | Highlight sweeping around the stroke |
| `BOUNCE` | Energetic scale with overshoot |

### CoachmarkConfig

| Property | Default | Description |
|----------|---------|-------------|
| `scrimOpacity` | `MEDIUM` | Darkness of the overlay (LIGHT, MEDIUM, DARK, EXTRA_DARK) |
| `scrimTapBehavior` | `DISMISS` | Action when tapping outside cutout |
| `backPressBehavior` | `DISMISS` | Action on back press |
| `showSkipButton` | `false` | Show skip button in tooltip |
| `showProgressIndicator` | `true` | Show step dots for sequences |
| `delayBeforeShow` | `0L` | Milliseconds to wait before showing |

### Controller Methods

```kotlin
controller.show(target)           // Show single coachmark
controller.showSequence(targets)  // Show multi-step tour
controller.next()                 // Advance to next step
controller.previous()             // Go back one step
controller.dismiss()              // Hide coachmark
controller.enabled = false        // Disable all coachmarks
```

## Sample App

The `sample` module includes interactive demos for all features. Clone the repo and run it to explore.

## Requirements

- Kotlin 1.9+
- Compose BOM 2024.01.00+
- Android API 23+

## License

```
Copyright 2024 Adit Lal

Licensed under the Apache License, Version 2.0
```
