# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Features
- **Web dashboard demo** — New `sample-web` module with a "Lumen Analytics" SaaS dashboard showcasing a 7-step coachmark tour with varied shapes, animations, and connectors
- **Connector cutout gap** — New `connectorCutoutGap` config property (default `12.dp`) that ensures the connector dot clears the cutout's outermost animation effect (glow rings, ripple rings, pulse/bounce scale)

### Bug Fixes
- Fixed connector dot overlapping cutout stroke during PULSE and BOUNCE animations — dot position now accounts for max animation scale
- Fixed connector dot rendering inside glow/ripple visual effects — gap calculation now considers the true outer bounds of each animation type

## [1.0.0-beta05] - 2026-02-16

### Features
- **Connector endpoint styles** — `DOT` (default), `ARROW`, `NONE`, and `CUSTOM` endpoint decorations where the connector meets the tooltip (#23)
- **Bezier curve connectors** — New `ConnectorStyle.CURVED` for smooth quadratic Bezier curve connectors (#23)
- **Tap-through behavior** — `TargetTapBehavior` enum (`PASS_THROUGH`, `ADVANCE`, `BOTH`) and `onTargetTap` callback on `CoachmarkHost` (#26)
- **Analytics callbacks** — `CoachmarkAnalytics` data class with `onShow`, `onDismiss`, `onAdvance`, `onComplete` callbacks and `DismissReason` enum (#32)
- **"Don't Show Again" checkbox** — `showDontShowAgain` and `persistKey` on `CoachmarkTarget`, backed by `CoachmarkRepository` persistence (#33)
- **Accessibility support** — TalkBack/VoiceOver with dynamic content descriptions, semantic headings, focus management, live regions, and 48dp touch targets (#34)

### Bug Fixes
- Fixed tooltip positioning for horizontal connectors (#24)
- Fixed horizontal connector endpoint appearing near cutout instead of near tooltip (#31)
- Fixed cutout not redrawing when advancing in a sequence (#34)

### Documentation
- Added connector endpoint styles, Bezier curves, and arrow configuration to guide and API reference
- Added tap-through, analytics, "don't show again", and accessibility sections to guide
- Added accessibility platform support matrix (Android, iOS, Desktop, Web)

### Sample App
- Added Tap-Through, Analytics, and Don't Show Again interactive examples
- Added `expect/actual` `rememberCoachmarkRepository()` for cross-platform repository initialization

## [1.0.0-beta04] - 2026-02-16

### Features
- **Desktop (JVM) support** — `jvm()` target with Compose Desktop compatibility
- **Web (wasmJs) support** — `wasmJs { browser() }` target for Kotlin/Wasm
- **Desktop sample app** — Sample runs as a desktop window via `desktopJar`
- **Web sample app** — Sample runs in browser via `wasmJsBrowserDistribution`
- **Per-publication javadoc JARs** — Maven Central compliance for JVM artifacts

### Infrastructure
- Desktop and Web CI jobs for platform validation

## [1.0.0-beta03] - 2026-02-10

### Features
- **Kotlin Multiplatform** — Library converted to KMP with iOS targets (iosArm64, iosX64, iosSimulatorArm64)
- **KMP sample app** — All example screens run on both Android and iOS
- **iOS app wrapper** — Xcode project for running the sample on iOS Simulator
- **Cross-platform back handling** — `expect/actual` `PlatformBackHandler`

## [1.0.0-beta02] - 2026-02-04

### Features
- **LazyColumn visibility support** — Coachmarks wait for targets to be visible in viewport before showing

### Bug Fixes
- Fixed back press not being intercepted in demo screens
- Fixed tooltip card theming — light theme now uses light card background with proper text contrast
- Fixed white-on-white text when `showTooltipCard = true`

### Documentation
- Added API Reference link and documentation website badge

## [1.0.0-beta01] - 2026-02-04

### Features
- 5 cutout shapes: Circle, Rect, RoundedRect, Squircle, Star
- 6 highlight animations: None, Pulse, Glow, Ripple, Shimmer, Bounce
- 5 connector styles: Vertical, Horizontal, Elbow, Direct, Auto
- Customizable tooltips with position control (Top, Bottom, Start, End, Auto)
- Multi-step sequences with progress indicators
- Dialog coordination with auto-dismiss
- Full theming via `CoachmarkColors`
- Scrim opacity presets: Light, Medium, Dark, Extra Dark
- 11 interactive sample app demos
