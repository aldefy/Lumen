# Lumen

<p align="center">
  <img src="logo.svg" width="128" height="128" alt="Lumen Logo"/>
</p>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.aldefy/lumen.svg?label=Maven%20Central&logo=apachemaven)](https://central.sonatype.com/artifact/io.github.aldefy/lumen)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose_Multiplatform-1.6-4285F4.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)

**Spotlight your UI, not a screenshot of it.**

A Compose Multiplatform coachmark library with transparent cutouts that let your real UI shine through - complete with live animations and touch events.

## Features

- **5 Cutout Shapes** - Circle, Rectangle, Rounded Rectangle, Squircle, Star
- **6 Highlight Animations** - None, Pulse, Glow, Ripple, Shimmer, Bounce
- **5 Connector Styles** - Vertical, Horizontal, Elbow, Direct, Auto
- **Customizable Tooltips** - Position, colors, CTA text, skip button
- **Multi-Step Sequences** - Tours with progress indicators
- **Dialog Coordination** - Auto-dismiss when dialogs appear

## Quick Start

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.aldefy:lumen:1.0.0-beta01")
}
```

```kotlin
@Composable
fun MyScreen() {
    val controller = rememberCoachmarkController()

    CoachmarkHost(controller = controller) {
        IconButton(
            onClick = { /* ... */ },
            modifier = Modifier.coachmarkTarget(controller, "settings")
        ) {
            Icon(Icons.Default.Settings, "Settings")
        }
    }

    LaunchedEffect(Unit) {
        controller.show(
            CoachmarkTarget(
                id = "settings",
                title = "Settings",
                description = "Customize your preferences here.",
                shape = CutoutShape.Circle(),
                connectorStyle = ConnectorStyle.ELBOW,
            )
        )
    }
}
```

## Cutout Shapes

```kotlin
CutoutShape.Circle(radiusPadding = 8.dp)
CutoutShape.Rect(padding = 8.dp)
CutoutShape.RoundedRect(cornerRadius = 12.dp, padding = 8.dp)
CutoutShape.Squircle(cornerRadius = 20.dp, padding = 8.dp)  // iOS-style
CutoutShape.Star(points = 5, innerRadiusRatio = 0.5f, padding = 8.dp)
```

## Connector Styles

```kotlin
ConnectorStyle.VERTICAL    // Straight down/up from target to tooltip
ConnectorStyle.HORIZONTAL  // Straight left/right from target to tooltip
ConnectorStyle.ELBOW       // L-shaped connector with 90° bend
ConnectorStyle.DIRECT      // Diagonal line pointing to tooltip
ConnectorStyle.AUTO        // Automatically picks the best style
```

## Highlight Animations

```kotlin
HighlightAnimation.NONE     // Static
HighlightAnimation.PULSE    // Breathing scale effect
HighlightAnimation.GLOW     // Radiating glow rings
HighlightAnimation.RIPPLE   // Expanding rings
HighlightAnimation.SHIMMER  // Orbiting highlight
HighlightAnimation.BOUNCE   // Energetic pop effect
```

## Multi-Step Sequences

```kotlin
controller.showSequence(
    listOf(
        CoachmarkTarget(id = "step1", title = "First", ctaText = "Next"),
        CoachmarkTarget(id = "step2", title = "Second", ctaText = "Next"),
        CoachmarkTarget(id = "step3", title = "Done!", ctaText = "Got it"),
    )
)
```

## Configuration

```kotlin
CoachmarkHost(
    controller = controller,
    config = CoachmarkConfig(
        scrimOpacity = ScrimOpacity.DARK,
        scrimTapBehavior = ScrimTapBehavior.DISMISS,
        backPressBehavior = BackPressBehavior.NAVIGATE,
        highlightAnimation = HighlightAnimation.PULSE,
        showSkipButton = true,
        showTooltipCard = false,
        delayBeforeShow = 500L,
    ),
    colors = CoachmarkColors(
        scrimColor = Color.Black.copy(alpha = 0.85f),
        strokeColor = Color.White,
        tooltipBackground = Color.White,
        titleColor = Color.Black,
        descriptionColor = Color.DarkGray,
        ctaButtonColor = Color(0xFF007AFF),
        ctaTextColor = Color.White,
    ),
)
```

## Tooltip Position

```kotlin
CoachmarkTarget(
    id = "target",
    title = "Title",
    description = "Description",
    tooltipPosition = TooltipPosition.BOTTOM,  // TOP, BOTTOM, START, END, AUTO
    connectorStyle = ConnectorStyle.VERTICAL,
    connectorLength = 60.dp,
)
```

## Dialog Coordination

```kotlin
val overlayCoordinator = rememberOverlayCoordinator()
val controller = rememberCoachmarkController(overlayCoordinator)

if (showDialog) {
    DialogOverlayEffect()  // Auto-dismisses coachmarks
    AlertDialog(...)
}
```

## Sample App

Check out the `sample` module for interactive demos of all features.

## Requirements

- Kotlin 1.9+
- Compose BOM 2024.01.00+
- Android API 23+ (Android 6.0)

## License

```
Copyright 2024 Adit Lal

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
