# Lumen

> Spotlight your UI, not a screenshot of it.

Lumen is a Compose Multiplatform coachmark library that creates **true transparent cutouts** in the overlay scrim. Your actual UI remains visible and interactive through the spotlight - animations play, buttons respond, nothing is faked.

> **Supports Android, iOS, Desktop (JVM), and Web (Wasm)** via Kotlin Multiplatform.

## Features

- **True Transparent Cutouts** - Uses `BlendMode.Clear` for genuine transparency
- **5 Cutout Shapes** - Circle, Rect, RoundedRect, Squircle, Star
- **6 Highlight Animations** - Pulse, Glow, Ripple, Shimmer, Bounce, or static
- **6 Connector Styles** - Straight, elbow, curved Bezier, and more
- **4 Connector End Styles** - Dot, arrow, none, or custom DrawScope rendering
- **Multi-Step Sequences** - Build onboarding tours with progress indicators
- **Dialog Coordination** - Automatically handles overlay conflicts
- **Full Theming** - Customize every color and dimension

## Quick Example

```kotlin
val controller = rememberCoachmarkController()

CoachmarkHost(controller = controller) {
    Button(
        onClick = { },
        modifier = Modifier.coachmarkTarget(controller, "my-button")
    ) {
        Text("Click me")
    }
}

// Trigger the coachmark
controller.show(
    CoachmarkTarget(
        id = "my-button",
        title = "Welcome",
        description = "Tap here to get started.",
    )
)
```

## Live Demo

Try the interactive [Wasm demo](demo/) in your browser — no install required.

## Get Started

Check out the [Getting Started](getting-started.md) guide to add Lumen to your project.
