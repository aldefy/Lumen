# Library Quality & Metrics Guide

> Best practices for building a production-quality Compose library

---

## Table of Contents

1. [Demo App Strategy](#1-demo-app-strategy)
2. [Binary Compatibility](#2-binary-compatibility)
3. [Compose Version Strategy](#3-compose-version-strategy)
4. [Library Metrics](#4-library-metrics)
5. [Performance Benchmarks](#5-performance-benchmarks)
6. [CI/CD Checklist](#6-cicd-checklist)

---

## 1. Demo App Strategy

### Purpose

A demo app serves three audiences:

| Audience | What They Need |
|----------|----------------|
| **Evaluators** | Quick "does this work?" validation before adding dependency |
| **Developers** | Real-world usage examples, copy-paste starting points |
| **Contributors** | Development environment, testing ground |

### Structure

```
compose-spotlight/
├── spotlight/                    # Library module
│   ├── src/main/kotlin/
│   └── build.gradle.kts
├── sample/                       # Demo app
│   ├── src/main/kotlin/
│   │   └── com/aditlal/sample/
│   │       ├── MainActivity.kt
│   │       ├── examples/
│   │       │   ├── BasicExample.kt
│   │       │   ├── SequenceExample.kt
│   │       │   ├── ShapesExample.kt
│   │       │   ├── ConnectorsExample.kt
│   │       │   ├── LazyColumnExample.kt
│   │       │   ├── DialogCoordinationExample.kt
│   │       │   └── CustomizationExample.kt
│   │       └── ui/
│   │           └── ExampleListScreen.kt
│   └── build.gradle.kts
└── README.md
```

### Demo App Features

#### Must Have
- [ ] **Example gallery** — List of all examples with descriptions
- [ ] **Isolated examples** — Each feature in its own composable
- [ ] **Reset button** — Reset coachmark state to replay
- [ ] **Configuration toggles** — Runtime switches for options
- [ ] **Code snippets** — Show relevant code alongside demo

#### Nice to Have
- [ ] **APK on GitHub Releases** — Download without building
- [ ] **Google Play listing** — Even higher discoverability
- [ ] **Dark mode toggle** — Show theming works
- [ ] **Screen recording** — Built-in GIF capture

### Example Code Pattern

```kotlin
@Composable
fun BasicExample() {
    val controller = rememberCoachmarkController()
    var showCoachmark by remember { mutableStateOf(false) }

    Column {
        // Demo controls
        Button(onClick = { showCoachmark = true }) {
            Text("Show Coachmark")
        }

        Button(onClick = {
            // Reset persistence for demo
            controller.reset()
        }) {
            Text("Reset")
        }

        // Actual usage example
        CoachmarkHost(controller = controller) {
            IconButton(
                onClick = { /* action */ },
                modifier = Modifier.coachmarkTarget(controller, "settings")
            ) {
                Icon(Icons.Default.Settings, "Settings")
            }
        }

        // Trigger
        LaunchedEffect(showCoachmark) {
            if (showCoachmark) {
                controller.show(
                    CoachmarkTarget(
                        id = "settings",
                        title = "Settings",
                        description = "Configure your preferences",
                        shape = CutoutShape.Circle(),
                    )
                )
                showCoachmark = false
            }
        }
    }
}
```

### Demo App Metrics Display

Show these metrics in a "Debug Info" section:

```kotlin
@Composable
fun DebugInfo() {
    val buildConfig = LocalBuildConfig.current

    Column {
        Text("Library version: ${BuildConfig.LIBRARY_VERSION}")
        Text("Compose BOM: ${BuildConfig.COMPOSE_BOM}")
        Text("Min SDK: ${BuildConfig.MIN_SDK}")
        Text("Kotlin: ${KotlinVersion.CURRENT}")
    }
}
```

---

## 2. Binary Compatibility

### The Problem

When you publish a library, consumers compile against your API. If you change public APIs between versions, their compiled code may break at runtime with:

```
java.lang.NoSuchMethodError: 'void Library.oldMethod()'
```

### What Breaks Binary Compatibility

Based on [Kotlin's guidelines](https://kotlinlang.org/docs/api-guidelines-backward-compatibility.html):

| Change | Breaks Binary? | Breaks Source? |
|--------|----------------|----------------|
| Adding non-default parameter | ✅ Yes | ✅ Yes |
| Adding parameter with default value | ✅ Yes | ❌ No |
| Removing public function | ✅ Yes | ✅ Yes |
| Narrowing return type (`Number` → `Int`) | ✅ Yes | ❌ No |
| Widening return type (`Int` → `Number`) | ✅ Yes | ✅ Yes |
| Adding data class property | ✅ Yes | ❌ No |
| Changing inline function body | ✅ Yes | ❌ No |
| Renaming parameter | ❌ No | ✅ Yes (named args) |

### Solution: Binary Compatibility Validator

[Kotlin Binary Compatibility Validator](https://github.com/Kotlin/binary-compatibility-validator) is a Gradle plugin that:

1. **Dumps API** — Creates `.api` file describing public API
2. **Checks changes** — Fails build if API changed incompatibly

#### Setup

```kotlin
// build.gradle.kts (root)
plugins {
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.16.3"
}

// Or with Kotlin 2.2.0+ (built-in)
kotlin {
    apiValidation {
        enabled = true
    }
}
```

#### Usage

```bash
# Generate API dump (commit this file)
./gradlew apiDump

# Check for breaking changes (runs on every build)
./gradlew apiCheck
```

#### Generated .api File

```
// spotlight.api
public final class com/aditlal/spotlight/CoachmarkController {
    public final fun dismiss()V
    public final fun getState()Lkotlinx/coroutines/flow/StateFlow;
    public final fun next()V
    public final fun previous()V
    public final fun show(Lcom/aditlal/spotlight/CoachmarkTarget;)Z
    public final fun showSequence(Ljava/util/List;)Z
}
```

### Best Practices

#### 1. Use Manual Overloads (Not Default Parameters)

```kotlin
// ❌ Breaks binary compatibility when adding params
fun show(target: Target, delay: Long = 0) { }

// ✅ Binary compatible - add new overload
fun show(target: Target) = show(target, delay = 0)
fun show(target: Target, delay: Long) { }
```

#### 2. Avoid Data Classes in Public API

```kotlin
// ❌ Data class - can't add properties safely
data class CoachmarkTarget(
    val id: String,
    val title: String,
)

// ✅ Regular class with builder or named params
class CoachmarkTarget private constructor(
    val id: String,
    val title: String,
    val description: String?,
) {
    class Builder(private val id: String) {
        private var title: String = ""
        private var description: String? = null

        fun title(title: String) = apply { this.title = title }
        fun description(desc: String?) = apply { this.description = desc }
        fun build() = CoachmarkTarget(id, title, description)
    }
}

// Or: Use @JvmOverloads carefully with explicit overloads
```

#### 3. Explicit Return Types

```kotlin
// ❌ Inferred type - can break if implementation changes
fun createController() = CoachmarkControllerImpl()

// ✅ Explicit type - stable contract
fun createController(): CoachmarkController = CoachmarkControllerImpl()
```

#### 4. Deprecation Cycle

```kotlin
// Version 1.1 - Deprecate
@Deprecated(
    message = "Use show(CoachmarkTarget) instead",
    replaceWith = ReplaceWith("show(target)"),
    level = DeprecationLevel.WARNING
)
fun showOld(id: String, title: String) { }

// Version 1.2 - Error level
@Deprecated(..., level = DeprecationLevel.ERROR)

// Version 2.0 - Remove (major version only)
```

#### 5. Use @RequiresOptIn for Experimental APIs

```kotlin
@RequiresOptIn(
    message = "This API is experimental and may change",
    level = RequiresOptIn.Level.WARNING
)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalSpotlightApi

@ExperimentalSpotlightApi
fun experimentalFeature() { }
```

---

## 3. Compose Version Strategy

### The Challenge

Your library depends on Compose. Users have different Compose versions. How do you avoid conflicts?

### Strategy: compileOnly + Documented Minimum

```kotlin
// spotlight/build.gradle.kts
dependencies {
    // Use compileOnly - don't force a version on consumers
    compileOnly("androidx.compose.ui:ui:1.6.0")
    compileOnly("androidx.compose.foundation:foundation:1.6.0")
    compileOnly("androidx.compose.material3:material3:1.2.0")

    // For testing, use specific version
    testImplementation(platform("androidx.compose:compose-bom:2024.01.00"))
    testImplementation("androidx.compose.ui:ui-test-junit4")
}
```

### Document Compatibility Matrix

In README:

```markdown
## Compatibility

| Spotlight | Compose BOM | Kotlin | Min SDK |
|-----------|-------------|--------|---------|
| 1.0.x     | 2024.01.00+ | 1.9.0+ | 23      |
| 1.1.x     | 2024.06.00+ | 2.0.0+ | 23      |
| 2.0.x     | 2025.01.00+ | 2.1.0+ | 24      |

### Kotlin Compiler Compatibility

With Kotlin 2.0+, no special configuration needed - Compose compiler
is bundled with Kotlin.

For Kotlin 1.9.x, add:
```kotlin
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.14"
}
```
```

### Version Conflict Resolution

Document how users can resolve conflicts:

```kotlin
// If you get version conflicts, force resolution:
configurations.all {
    resolutionStrategy {
        force("androidx.compose.ui:ui:1.6.0")
    }
}

// Or use BOM (recommended)
dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("com.aditlal:compose-spotlight:1.0.0")
}
```

---

## 4. Library Metrics

### What to Measure

#### Size Metrics

| Metric | What It Measures | Target | Tool |
|--------|------------------|--------|------|
| **AAR Size** | Raw library size | < 100 KB | `ls -la *.aar` |
| **Method Count** | DEX methods added | < 500 | [dexcount-gradle-plugin](https://github.com/KeepSafe/dexcount-gradle-plugin) |
| **APK Impact** | Size added to consumer app | < 50 KB | [apkscale](https://github.com/nickcaballero/apkscale) |
| **Dependency Count** | Transitive deps | Minimal | `./gradlew dependencies` |

#### Code Metrics

| Metric | What It Measures | Target |
|--------|------------------|--------|
| **Lines of Code** | Library size | Document, not target |
| **Public API Surface** | Exposed classes/functions | Minimal |
| **Test Coverage** | Code tested | > 80% |
| **Cyclomatic Complexity** | Code complexity | < 10 per function |

### Tools Setup

#### 1. Dexcount Gradle Plugin

```kotlin
// build.gradle.kts
plugins {
    id("com.getkeepsafe.dexcount") version "4.0.0"
}

// Output after build:
// Total methods in spotlight-release.aar: 342
// Total fields in spotlight-release.aar: 89
```

#### 2. AAR Size in CI

```yaml
# .github/workflows/size.yml
- name: Measure AAR size
  run: |
    ./gradlew :spotlight:assembleRelease
    SIZE=$(ls -la spotlight/build/outputs/aar/spotlight-release.aar | awk '{print $5}')
    echo "AAR_SIZE=$SIZE" >> $GITHUB_ENV
    echo "## Library Size" >> $GITHUB_STEP_SUMMARY
    echo "AAR: $(($SIZE / 1024)) KB" >> $GITHUB_STEP_SUMMARY
```

#### 3. APK Impact Measurement

```kotlin
// Create a minimal test app
// app-baseline/: Empty app without library
// app-with-lib/: Same app with library

// Compare APK sizes
val baseline = File("app-baseline/build/outputs/apk/release/app-release.apk").length()
val withLib = File("app-with-lib/build/outputs/apk/release/app-release.apk").length()
val impact = withLib - baseline
println("Library adds ${impact / 1024} KB to APK")
```

### Metrics Dashboard (README Badge)

```markdown
![AAR Size](https://img.shields.io/badge/AAR-45KB-green)
![Methods](https://img.shields.io/badge/Methods-342-blue)
![Min SDK](https://img.shields.io/badge/Min%20SDK-23-orange)
```

### What Users Care About

Based on library evaluation patterns:

| Priority | Metric | Why |
|----------|--------|-----|
| 1 | **APK Impact** | "How much will this bloat my app?" |
| 2 | **Method Count** | "Will this push me over 64K?" |
| 3 | **Min SDK** | "Does this work for my users?" |
| 4 | **Compose Version** | "Will this conflict with my deps?" |
| 5 | **Kotlin Version** | "Is this compatible with my project?" |
| 6 | **Transitive Deps** | "What else am I pulling in?" |

---

## 5. Performance Benchmarks

### Compose-Specific Concerns

For a coachmark library, key performance areas:

| Area | Concern | How to Measure |
|------|---------|----------------|
| **Scrim rendering** | Canvas draw time | Macrobenchmark |
| **Cutout calculation** | Path operations | Trace sections |
| **Animation smoothness** | Frame drops | FrameTimingMetric |
| **Recomposition** | Unnecessary redraws | Layout Inspector |

### Macrobenchmark Setup

```kotlin
// benchmark/src/androidTest/kotlin/SpotlightBenchmark.kt
@LargeTest
@RunWith(AndroidJUnit4::class)
class SpotlightBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun coachmarkAppear() = benchmarkRule.measureRepeated(
        packageName = "com.aditlal.sample",
        metrics = listOf(
            FrameTimingMetric(),
            TraceSectionMetric("CoachmarkScrim"),
        ),
        iterations = 5,
        startupMode = StartupMode.WARM,
    ) {
        startActivityAndWait()
        device.findObject(By.text("Show Coachmark")).click()
        device.waitForIdle()
    }

    @Test
    fun coachmarkSequence() = benchmarkRule.measureRepeated(
        packageName = "com.aditlal.sample",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
    ) {
        startActivityAndWait()
        device.findObject(By.text("Show Sequence")).click()

        // Step through 5-step sequence
        repeat(5) {
            device.findObject(By.text("Next")).click()
            device.waitForIdle()
        }
    }
}
```

### Trace Sections in Library Code

```kotlin
// Add trace sections for profiling
import androidx.tracing.trace

@Composable
fun CoachmarkScrim(...) {
    trace("CoachmarkScrim") {
        Canvas(modifier = Modifier.fillMaxSize()) {
            trace("DrawScrim") {
                drawRect(scrimColor)
            }
            trace("DrawCutout") {
                drawPath(cutoutPath, blendMode = BlendMode.Clear)
            }
            trace("DrawConnector") {
                drawConnectorLine(...)
            }
        }
    }
}
```

### Recomposition Tracking

```kotlin
// Debug composable for development
@Composable
fun RecompositionTracker(name: String) {
    val recomposeCount = remember { mutableIntStateOf(0) }
    SideEffect { recomposeCount.intValue++ }

    if (BuildConfig.DEBUG) {
        Text(
            text = "$name: ${recomposeCount.intValue}",
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
```

### Performance Targets

| Metric | Target | Red Flag |
|--------|--------|----------|
| Frame time (P50) | < 8ms | > 16ms |
| Frame time (P99) | < 16ms | > 32ms |
| Recomposition count | Stable | Always increasing |
| GC during animation | 0 | Any |

---

## 6. CI/CD Checklist

### GitHub Actions Workflow

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Build library
        run: ./gradlew :spotlight:assembleRelease

      - name: Run unit tests
        run: ./gradlew :spotlight:testReleaseUnitTest

      - name: Check binary compatibility
        run: ./gradlew apiCheck

      - name: Measure library size
        run: |
          SIZE=$(stat -f%z spotlight/build/outputs/aar/spotlight-release.aar)
          echo "## Metrics" >> $GITHUB_STEP_SUMMARY
          echo "- AAR Size: $((SIZE / 1024)) KB" >> $GITHUB_STEP_SUMMARY

      - name: Count methods
        run: ./gradlew :spotlight:countReleaseDexMethods

  ui-tests:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Run UI tests
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 30
          script: ./gradlew :sample:connectedAndroidTest

  publish:
    needs: [build, ui-tests]
    if: startsWith(github.ref, 'refs/tags/v')
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Publish to Maven Central
        run: ./gradlew publish
        env:
          OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
          OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
          SIGNING_KEY: ${{ secrets.SIGNING_KEY }}
          SIGNING_PASSWORD: ${{ secrets.SIGNING_PASSWORD }}
```

### PR Checks

Every PR should verify:

- [ ] Library builds successfully
- [ ] Unit tests pass
- [ ] API compatibility check passes
- [ ] Sample app builds
- [ ] No increase in library size > 10%

### Release Checklist

- [ ] Update version in `gradle.properties`
- [ ] Update CHANGELOG.md
- [ ] Run `./gradlew apiDump` and commit `.api` file
- [ ] Create GitHub release with tag `v1.x.x`
- [ ] Publish to Maven Central
- [ ] Update README compatibility table

---

## Summary: Metrics to Display in README

```markdown
## Library Stats

| Metric | Value |
|--------|-------|
| AAR Size | 45 KB |
| Method Count | 342 |
| Min SDK | 23 |
| Compose BOM | 2024.01.00+ |
| Kotlin | 1.9.0+ |
| Dependencies | 0 runtime* |

*Uses Compose as `compileOnly` — no version forced on your project.
```

---

## References

- [Kotlin Binary Compatibility Guidelines](https://kotlinlang.org/docs/api-guidelines-backward-compatibility.html)
- [Kotlin Binary Compatibility Validator](https://github.com/Kotlin/binary-compatibility-validator)
- [Compose BOM](https://developer.android.com/develop/ui/compose/bom)
- [Dexcount Gradle Plugin](https://github.com/KeepSafe/dexcount-gradle-plugin)
- [APK Analyzer](https://developer.android.com/studio/debug/apk-analyzer)
- [Compose Performance Guide](https://developer.android.com/develop/ui/compose/performance)
- [Macrobenchmark](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview)

---

*Last updated: February 2026*
