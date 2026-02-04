# Getting Started

## Installation

Add the dependency to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.aldefy:lumen:1.0.0-beta02")
}
```

For Gradle Groovy:

```groovy
dependencies {
    implementation 'io.github.aldefy:lumen:1.0.0-beta02'
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

## Next Steps

- Learn about [Multi-Step Sequences](guide.md#multi-step-sequences)
- Explore [Cutout Shapes](guide.md#cutout-shapes)
- Customize with [Theming](guide.md#theming)
