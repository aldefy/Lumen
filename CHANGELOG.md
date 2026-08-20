# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Build
- Updated toolchain: Kotlin 2.0.21 -> 2.3.0, Compose Multiplatform 1.7.3 -> 1.9.3, AGP 8.2.2 -> 8.13.2, Gradle 8.9 -> 8.14.3
- Updated dependencies: activity-compose 1.8.2 -> 1.12.2, kotlinx-coroutines 1.8.0 -> 1.9.0, atomicfu 0.25.0 -> 0.29.0, binary-compatibility-validator 0.16.3 -> 0.17.0
- Raised `compileSdk` to 36 (`:lumen`) and `compileSdk`/`targetSdk` to 36 (`:sample`)
- Migrated both wasmJs sample entrypoints from `CanvasBasedWindow` to `ComposeViewport`. Compose Multiplatform 1.9.3 raises the `CanvasBasedWindow` deprecation to an error, so this is required to compile. `ComposeViewport` takes a parent container and creates the canvas itself, so both `index.html` files now use `<div id="ComposeTarget">` instead of `<canvas>`.
- Re-recorded the 10 `scrim_*` screenshot goldens: Compose Multiplatform 1.9.3 no longer reserves system-bar insets in the Robolectric test surface, so the captured canvas is now the full 2400px implied by `w400dp-h800dp-xxhdpi` (previously 2232px). No library rendering change.

### Notes for consumers
- **Android consumers now need `compileSdk` 36 and AGP 8.9.1 or newer.** This is required by `activity-compose` 1.12.2, whose AAR metadata declares `minCompileSdk=36` and `minAndroidGradlePluginVersion=8.9.1`. Builds on older toolchains will fail `checkDebugAarMetadata`. `minSdk` is unchanged at 23, so no runtime device support is dropped.
- Robolectric remains pinned to `sdk=34` in `lumen/src/androidUnitTest/resources/robolectric.properties`. Robolectric 4.14.1 supports SDK 34 at most, and even 4.15.1 only reaches SDK 35, so unit tests cannot yet execute against SDK 36.

## [1.0.0-beta20] - 2026-08-20

### Bug Fixes
- `beta19`'s Kotlin 2.3.0 toolchain bump was forcing that same version onto every consumer app: the published klib/jar metadata was stamped `mv=[2,3,0]`, which older Kotlin compilers refuse to read at all. Pinned `apiVersion`/`languageVersion` to `KOTLIN_2_1` in `lumen/build.gradle.kts` so the library still builds with the 2.3.0 toolchain but emits `2.1`-compatible metadata — verified by inspecting the `kotlin.Metadata.mv` annotation on the built jar (now `[2,1,0]`, previously `[2,3,0]`). Consumers on Kotlin 2.1 or newer can depend on this release; 2.1 is the new floor, not "any version." Raise the pin only as a deliberate decision to drop older-Kotlin consumers.

## [1.0.0-beta19] - 2026-08-20

### Build
- Fixed Maven Central publishing for real: `1.0.0-beta18` published incomplete (only `lumen-iosx64` and `lumen-iossimulatorarm64` — Android/JVM/wasmJs/the KMP metadata module never made it), because the OSSRH-compatibility endpoint stages each Gradle publish task as its own implicit deployment and only the most recently uploaded one gets released. Switched to a single atomic multipart bundle upload against Central's official Publisher API instead (the same pattern `aldefy/composeproof` already uses successfully) — `lumen/build.gradle.kts` no longer configures a remote Sonatype Maven repository at all, `release.yml` zips the local-staging output and uploads it in one `curl --form bundle` call. Verified all 7 expected publications are present in the local-staging output before this release.

### Known issue
- **`1.0.0-beta18` is permanently incomplete on Maven Central** — do not depend on it. Central doesn't allow re-publishing a version, so this can't be fixed retroactively; use `1.0.0-beta19` or later.

## [1.0.0-beta18] - 2026-08-20

### Build
- Fixed the release pipeline: `s01.oss.sonatype.org` (OSSRH) shut down 2025-06-30, and `release.yml` had never actually published to Sonatype in the first place (only to a local staging dir, zipped for the GitHub release). Now publishes to the Central Portal's OSSRH-compatibility staging API and transfers the deployment into the Portal proper. No library code changes — this release exists to verify the fixed pipeline end-to-end.

## [1.0.0-beta17] - 2026-08-20

### Features
- `CoachmarkConfig.tooltipShape`: an optional `Shape` factory for the tooltip card, receiving the target's horizontal anchor and whether the tooltip is above or below it. A new `io.luminos.shapes.SpeechBubbleShape` builds a speech-bubble tail as one continuous outline with the card body — no seam, no separate connector needed. `CoachmarkConfig.tooltipTailInset` reserves the extra space the tail needs.
- `ConnectorStyle.CUSTOM` + `CoachmarkTarget.customConnector`: a per-target full-control connector renderer, taking priority over `CoachmarkConfig.customConnector` (added in beta16) when both are set. Lets a single step in a sequence override what the rest of the sequence uses — e.g. mixing a teardrop-tail step with elbow-connector steps. `CUSTOM` with no lambda supplied anywhere falls back to a normal connector line rather than drawing nothing.

### Bug Fixes
- `DarkCoachmarkColors.tooltipCardColor` and the default `CoachmarkColors().tooltipCardColor` failed WCAG contrast against `scrimColor` (measured 1.19:1 and 1.38:1; need ≥3:1 for a UI boundary) — both defaulted to a near-black card over a near-black scrim, making the card edge (and now the speech-bubble tail) nearly invisible in dark themes. The default's `titleColor = Color.Black` additionally failed text contrast outright against its own dark card (1.38:1, needs ≥4.5:1) — a pre-existing defect, previously undetected because no sample exercised `showTooltipCard = true` with the plain default colors. Fixed by moving both cards to `#666666` and the default's title/description colors to white/near-white. Verified ≥3.1:1 card-vs-scrim and ≥6.2:1 text-vs-card in both palettes; `LightCoachmarkColors` was already fine and is untouched.

### Sample App
- New "Speech Bubble Tail" step in the Custom Rendering example, and a "Custom Rendering" example itself demonstrating `progressIndicator`, `tooltipShape`, and `CutoutShape.Custom` together with no library-specific code beyond the extension points.
- LazyColumn example's "Item Cards" step now uses `tooltipShape` on a real whole-row target, matching a reference speech-bubble tooltip design (tooltip above a list row, tail pointing down at it, no connector line).

### Known limitations
- `tooltipShape`/`SpeechBubbleShape` only support a tail on the top or bottom edge today. Combining `tooltipShape` with a `HORIZONTAL`-style connector (target beside the tooltip rather than above/below it) produces a tail pointing in a direction unrelated to where the connector line actually goes — confirmed by direct on-device testing. Left as a follow-up rather than a partial fix.

## [1.0.0-beta16] - 2026-08-20

### Features
- Three extension points so app- and brand-specific rendering no longer needs a new enum case or config field in `:lumen`:
  - `CoachmarkConfig.progressIndicator`: an optional `@Composable` slot replacing the built-in progress dots. `null` (default) keeps the existing dots.
  - `CoachmarkConfig.customConnector`: an optional `DrawScope` lambda that takes over the entire connector — line and endpoint — given the cutout-edge anchor, tooltip-edge anchor, and reveal progress. `ConnectorStyle`/`ConnectorEndStyle` are ignored while set.
  - `CutoutShape.Custom`: a `pathBuilder` case for cutout outlines that aren't one of the built-ins (Circle/RoundedRect/Rect/Squircle/Star). Draws through the same `BlendMode.Clear` offscreen-layer path as every built-in shape.
- `ProgressIndicatorStyle.PILL`: the current step in a multi-step progress indicator renders as an elongated pill while the other steps stay small dots, like an iOS-style page indicator. `CoachmarkConfig.progressIndicatorStyle` (default `DOTS`, preserving the existing look) and `CoachmarkConfig.progressActivePillWidth` control it — kept as a built-in convenience on top of the new `progressIndicator` slot, since it's a generic enough idiom to ship directly.

### Sample App
- Added a "Custom Rendering" example demonstrating all three extension points together: a pill progress indicator, a teardrop speech-bubble connector, and a diamond cutout shape — none of which required a library change.
- Added a "Start Tour (Pill)" button to the Multi-Step Sequence example to demo `ProgressIndicatorStyle.PILL`.

## [1.0.0-beta15] - 2026-03-18

### Features
- Per-element text alignment: `titleTextAlign`, `descriptionTextAlign`, `skipButtonTextAlign`, `dontShowAgainTextAlign` on both `CoachmarkConfig` and `CoachmarkTarget`
- `ctaHorizontalArrangement` to control CTA button horizontal positioning (Start/Center/End)
- Description and CTA row auto-indent to align with title when `titleInlineWithConnector` is active
- Existing `tooltipTextAlign` preserved as fallback for backward compatibility

### Sample App
- Reworked TextAlignmentExample with per-element alignment chip selectors and toggle switches for Skip Button and Title Inline With Connector

## [1.0.0-beta14] - 2026-03-18

### Sample App
- Added single coachmark demo with text alignment icon row in TextAlignmentExample
- Reduced contact list spacer items from 8 to 4

## [1.0.0-beta13] - 2026-03-17

### Bug Fixes
- Fixed title text not aligning with connector dot for tooltip-above-target — title is now indented to match the dot's X position
- Forced `TextAlign.Start` on title when `titleInlineWithConnector` is active, preventing `tooltipTextAlign` from breaking dot-title alignment

## [1.0.0-beta12] - 2026-03-17

### Bug Fixes
- Fixed title text alignment for tooltip-above-target — when `titleInlineWithConnector` is enabled, the title left edge now aligns with the connector dot position

## [1.0.0-beta11] - 2026-03-16

### Bug Fixes
- Fixed inline connector dot alignment for tooltip-above-target — when `titleInlineWithConnector` is enabled and the tooltip appears above the target, the connector dot now renders at the bottom of the tooltip aligned with the target center, with the connector line drawing directly to the measured dot position

## [1.0.0-beta10] - 2026-03-05

### Bug Fixes
- Fixed inline title connector line not reaching the composed dot — connector endpoint now uses the dot's actual measured position via `onGloballyPositioned` instead of estimating offsets, ensuring the line always connects to the dot regardless of skip button or other content above it

## [1.0.0-beta09] - 2026-03-05

### Features
- **Tooltip text alignment** — New `tooltipTextAlign` property on `CoachmarkConfig` (global default) and `CoachmarkTarget` (per-target override) to control text alignment for title, description, and other tooltip text elements. Supports `TextAlign.Start` (default), `TextAlign.Center`, and `TextAlign.End`
- **Inline title with connector** — New `titleInlineWithConnector` property on `CoachmarkConfig` and `CoachmarkTarget` that renders the tooltip title beside the connector dot on the same horizontal line instead of below it. Active only for vertical connectors. The canvas-drawn dot is automatically suppressed when inline mode is active, replaced by a composed dot indicator for pixel-perfect alignment

## [1.0.0-beta08] - 2026-02-25

### Bug Fixes
- Fixed horizontal connector tooltip positioning — tooltip is now vertically centered on the target instead of top-aligned, preventing text from rendering below targets near the bottom of the screen

## [1.0.0-beta07] - 2026-02-23

### Features
- **Retry skipped targets** — New `retrySkippedTargets` config flag that re-queues off-screen coachmark targets to the end of the sequence instead of permanently skipping them, with infinite-loop guard
- **Auto-scroll support** — `scrollRequester` callback on `CoachmarkController` enables auto-scrolling LazyColumn/LazyRow to bring off-screen targets into view before showing coachmarks
- **Bounds guard** — Scrim no longer flashes when target bounds haven't been laid out yet (Rect.Zero early return)

### Bug Fixes
- Fixed coachmark targets in scrollable lists being permanently skipped when scrolled off-screen
- Fixed docs site broken demo links

## [1.0.0-beta06] - 2026-02-17

### Features
- **CTA button customization** — New `ctaMinWidth`, `ctaMinHeight`, and `ctaCornerRadius` properties on `CoachmarkConfig` to control CTA button dimensions and shape (#40)
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
