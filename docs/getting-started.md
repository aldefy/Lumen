# Getting Started

## Installation

### Kotlin Multiplatform

Add to your shared module's `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.aldefy:lumen:1.0.0-beta03")
        }
    }
}
```

This resolves the correct artifact per target automatically:

| Target | Artifact |
|--------|----------|
| Android | `lumen-android` (AAR) |
| iOS arm64 | `lumen-iosarm64` (klib) |
| iOS Simulator arm64 | `lumen-iossimulatorarm64` (klib) |
| iOS Simulator x64 | `lumen-iosx64` (klib) |

### Android Only

If your project is not using KMP:

```kotlin
dependencies {
    implementation("io.github.aldefy:lumen-android:1.0.0-beta03")
}
```

Gradle Groovy:

```groovy
dependencies {
    implementation 'io.github.aldefy:lumen-android:1.0.0-beta03'
}
```

## Quick Start

### 1. Create a Controller

```kotlin
val controller = rememberCoachmarkController()
```

### 2. Wrap Your Content

```kotlin
CoachmarkHost(controller = controller) {
    // Your screen content goes here
    MyScreenContent()
}
```

### 3. Tag Your Targets

```kotlin
Button(
    onClick = { },
    modifier = Modifier.coachmarkTarget(controller, "unique-id")
) {
    Text("Click me")
}
```

### 4. Show the Coachmark

```kotlin
controller.show(
    CoachmarkTarget(
        id = "unique-id",
        title = "Welcome!",
        description = "This button does something amazing.",
    )
)
```

## Complete Example

```kotlin
@Composable
fun OnboardingScreen() {
    val controller = rememberCoachmarkController()
    var showCoachmark by remember { mutableStateOf(false) }

    CoachmarkHost(controller = controller) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { },
                    modifier = Modifier.coachmarkTarget(controller, "fab")
                ) {
                    Icon(Icons.Default.Add, "Add")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { showCoachmark = true }) {
                    Text("Start Tour")
                }
            }
        }
    }

    LaunchedEffect(showCoachmark) {
        if (showCoachmark) {
            controller.show(
                CoachmarkTarget(
                    id = "fab",
                    title = "Add Items",
                    description = "Tap here to add new items to your list.",
                    shape = CutoutShape.Circle(radiusPadding = 12.dp),
                    highlightAnimation = HighlightAnimation.PULSE,
                )
            )
            showCoachmark = false
        }
    }
}
```

## Platform Support

| Platform | Min Version |
|----------|-------------|
| Android  | API 23      |
| iOS arm64 | iOS 16     |
| iOS Simulator arm64 | iOS 16 |
| iOS Simulator x64   | iOS 16 |

Lumen is a Kotlin Multiplatform library. Add it to `commonMain` dependencies and it works on both Android and iOS. All APIs are identical across platforms.

## Next Steps

- Learn about [Multi-Step Sequences](guide.md#multi-step-sequences)
- Explore [Cutout Shapes](guide.md#cutout-shapes)
- Customize with [Theming](guide.md#theming)
