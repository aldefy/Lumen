# Roadmap: Compose Spotlight

> The first Compose Multiplatform coachmark library with true transparent cutouts, smart connectors, and production-ready UX.

---

## Why This Library Exists

We analyzed every Compose coachmark library and found the same gaps everywhere:

| Pain Point | Other Libraries | Compose Spotlight |
|------------|-----------------|-------------------|
| "Cutouts are just dimmed, not transparent" | Semi-transparent overlay | **True BlendMode.Clear cutouts** |
| "Only circles or rectangles" | 1-2 shapes | **4 shapes including Squircle** |
| "No visual connection to tooltip" | No connectors | **5 connector styles with AUTO** |
| "Breaks when dialogs appear" | No coordination | **OverlayCoordinator pattern** |
| "Back button dismisses everything" | No back handling | **BackPressBehavior.NAVIGATE** |
| "Doesn't work in BottomSheets" | Known bugs | **Dialog-aware architecture** |
| "Android only" | Limited KMP | **95% commonMain, true KMP** |

---

## Version Roadmap

### v1.0.0 — Foundation (Launch)

**Status:** Ready

The complete foundation for production coachmarks:

#### Core Features
- [x] **True Transparent Cutouts** — `BlendMode.Clear` + `CompositingStrategy.Offscreen`
- [x] **4 Cutout Shapes** — Circle, RoundedRect, Rect, Squircle (iOS-style)
- [x] **5 Connector Styles** — AUTO, DIRECT, HORIZONTAL, VERTICAL, ELBOW
- [x] **Multi-Step Sequences** — Progress indicator with dot navigation
- [x] **Smart Tooltip Positioning** — AUTO adapts to available screen space

#### State Management
- [x] **CoachmarkController** — Reactive StateFlow-based state machine
- [x] **Global Enable/Disable** — Skip coachmarks for deep links, test users, etc.
- [x] **Dialog Coordination** — Auto-dismiss/block when dialogs appear
- [x] **Stable Recomposition** — No duplicate triggers on UI redraws

#### User Interactions
- [x] **Scrim Tap Behaviors** — DISMISS, ADVANCE, or NONE
- [x] **Back Press Navigation** — DISMISS or NAVIGATE (step-by-step back)
- [x] **CTA Button** — Customizable text and colors

#### Developer Experience
- [x] **Modifier-based API** — `Modifier.coachmarkTarget(controller, "id")`
- [x] **CompositionLocal** — No prop drilling in LazyColumn
- [x] **CoachmarkRepository** — Persistence for "shown once" behavior
- [x] **Theming** — Light/dark mode with customizable colors

---

### v1.1.0 — Polish & Animation

**Status:** In Development

Bringing the library to life with motion and better UX:

#### Highlight Animations
- [ ] **Pulse Animation** — Gentle breathing effect on cutout (scale 1.0 → 1.05)
- [ ] **Glow Animation** — Animated stroke glow around cutout
- [ ] **Heartbeat Animation** — Quick double-pulse for urgency

```kotlin
CoachmarkTarget(
    highlightAnimation = HighlightAnimation.PULSE,
    // ...
)
```

#### Sequence UX Improvements
- [ ] **Skip Tour Button** — "Skip" option in tooltip for multi-step sequences
- [ ] **onSkipTour Callback** — Analytics hook when user skips
- [ ] **Delay Before Show** — Configurable delay to let UI settle

```kotlin
CoachmarkConfig(
    showSkipButton = true,
    delayBeforeShow = 500.milliseconds,
    onSkipTour = { step, total -> analytics.track("tour_skipped", step) },
)
```

#### Connector Enhancements
- [ ] **Arrow End Style** — Optional arrow head on connector
- [ ] **Custom Connector End** — DrawScope lambda for custom shapes
- [ ] **Curved Connectors** — Bezier curve option

```kotlin
enum class ConnectorEndStyle {
    DOT,      // Current default
    ARROW,    // Directional arrow
    NONE,     // Just the line
    CUSTOM,   // User-provided DrawScope
}
```

---

### v1.2.0 — Interaction & Accessibility

**Status:** Planned

Making coachmarks interactive and accessible to everyone:

#### Target Interactions
- [ ] **Tap-Through on Target** — Execute actual button action, not just advance
- [ ] **Target Tap Behaviors** — ADVANCE, PASS_THROUGH, or BOTH

```kotlin
CoachmarkTarget(
    targetTapBehavior = TargetTapBehavior.BOTH, // Advance AND execute action
)
```

#### User Preferences
- [ ] **"Don't Show Again" Checkbox** — Per-coachmark opt-out
- [ ] **"Show Once" Built-in** — Automatic persistence integration

```kotlin
CoachmarkTarget(
    showDontShowAgain = true,
    persistKey = "settings_coachmark", // Auto-saves to repository
)
```

#### Accessibility (a11y)
- [ ] **TalkBack Support** — Proper announcements for screen readers
- [ ] **VoiceOver Support** — iOS accessibility
- [ ] **LiveRegion Integration** — Assertive announcements
- [ ] **Focus Management** — Proper focus trap in tooltip

```kotlin
// Automatically announces:
// "Tutorial step 1 of 3: Settings. Customize your preferences here.
//  Button: Next. Button: Skip tour."
```

#### Analytics Callbacks
- [ ] **onShow** — When coachmark becomes visible
- [ ] **onDismiss** — When dismissed (with reason)
- [ ] **onAdvance** — When user advances to next step
- [ ] **onComplete** — When sequence finishes

```kotlin
CoachmarkHost(
    controller = controller,
    analytics = CoachmarkAnalytics(
        onShow = { target, step -> mixpanel.track("coachmark_shown", target.id) },
        onDismiss = { target, reason -> /* SCRIM_TAP, BACK_PRESS, CTA, SKIP */ },
        onComplete = { sequenceId -> mixpanel.track("tour_completed") },
    ),
)
```

---

### v1.3.0 — Advanced Features

**Status:** Planned

Power features for complex use cases:

#### DSL Builder
- [ ] **Sequence DSL** — Fluent builder for multi-step tours

```kotlin
coachmarkController.tour("onboarding") {
    step("settings") {
        title = "Settings"
        description = "Customize your preferences"
        shape = circle(radiusPadding = 12.dp)
        connector = elbow()
    }
    step("profile") {
        title = "Profile"
        description = "View your account"
        shape = squircle()
    }
    onComplete { analytics.track("onboarding_done") }
}
```

#### Conditional Coachmarks
- [ ] **Feature Flag Integration** — Show based on remote config
- [ ] **User Segment Targeting** — New users vs returning

```kotlin
coachmarkController.showIf(
    condition = { userSegment == "new" && !hasSeenOnboarding },
    target = onboardingTarget,
)
```

#### Rich Content
- [ ] **Custom Tooltip Content** — Full composable slot
- [ ] **Image in Tooltip** — Icon or illustration support
- [ ] **Video Thumbnail** — Preview with play button

```kotlin
CoachmarkTarget(
    customContent = { target, onNext, onDismiss ->
        Column {
            Image(painterResource(R.drawable.tip_illustration))
            Text(target.title)
            Button(onClick = onNext) { Text("Got it") }
        }
    },
)
```

---

### v2.0.0 — Compose Multiplatform

**Status:** Future

True cross-platform coachmarks:

#### Platform Support
- [x] **Android** — Full feature parity (shipping)
- [x] **iOS** — Compose Multiplatform for iOS (shipping)
- [ ] **Desktop** — macOS, Windows, Linux
- [ ] **Web** — Compose for Web (Wasm)

#### Platform Abstractions
- [x] **Interface-based Persistence** (CoachmarkStorage interface, not expect/actual)
  - [x] Android: SharedPreferences
  - [x] iOS: NSUserDefaults
  - [ ] Desktop: java.util.prefs
  - [ ] Web: localStorage

- [ ] **expect/actual for Haptics**
  - Android: VibrationEffect
  - iOS: UIImpactFeedbackGenerator
  - Desktop/Web: No-op

#### Module Structure
```
compose-spotlight/
├── spotlight-core/           # commonMain (95% of code)
├── spotlight-android/        # Android-specific
├── spotlight-ios/            # iOS-specific
├── spotlight-desktop/        # Desktop-specific
├── spotlight-web/            # Web/Wasm-specific
├── sample-android/
├── sample-ios/
├── sample-desktop/
└── sample-web/
```

---

## Feature Comparison Matrix

| Feature | v1.0 | v1.1 | v1.2 | v1.3 | v2.0 |
|---------|------|------|------|------|------|
| True transparent cutouts | ✅ | ✅ | ✅ | ✅ | ✅ |
| 4 cutout shapes | ✅ | ✅ | ✅ | ✅ | ✅ |
| 5 connector styles | ✅ | ✅ | ✅ | ✅ | ✅ |
| Multi-step sequences | ✅ | ✅ | ✅ | ✅ | ✅ |
| Dialog coordination | ✅ | ✅ | ✅ | ✅ | ✅ |
| Back press navigation | ✅ | ✅ | ✅ | ✅ | ✅ |
| Pulse/glow animation | | ✅ | ✅ | ✅ | ✅ |
| Skip tour button | | ✅ | ✅ | ✅ | ✅ |
| Arrow connectors | | ✅ | ✅ | ✅ | ✅ |
| Tap-through target | | | ✅ | ✅ | ✅ |
| Accessibility (a11y) | | | ✅ | ✅ | ✅ |
| Analytics callbacks | | | ✅ | ✅ | ✅ |
| DSL builder | | | | ✅ | ✅ |
| Custom tooltip content | | | | ✅ | ✅ |
| iOS support | ✅ | ✅ | ✅ | ✅ | ✅ |
| Desktop support | | | | | ✅ |
| Web support | | | | | ✅ |

---

## Competitive Landscape

### Why Not [Existing Library]?

| Library | Stars | Issue | Our Solution |
|---------|-------|-------|--------------|
| [canopas/compose-intro-showcase](https://github.com/canopas/compose-intro-showcase) | ~600 | [#37](https://github.com/canopas/compose-intro-showcase/issues/37) "Rectangular shape" requested | **4 shapes including Squircle** |
| | | [#35](https://github.com/canopas/compose-intro-showcase/issues/35) "Is this KMP?" — No | **KMP-ready architecture** |
| | | [#29](https://github.com/canopas/compose-intro-showcase/issues/29) "Doesn't work in BottomSheet" | **OverlayCoordinator** |
| | | [#27](https://github.com/canopas/compose-intro-showcase/issues/27) "Repeats on recomposition" | **Stable StateFlow** |
| [svenjacobs/reveal](https://github.com/svenjacobs/reveal) | ~250 | [#246](https://github.com/svenjacobs/reveal/issues/246) "Doesn't work with ModalBottomSheet" | **Dialog-aware** |
| | | [#105](https://github.com/svenjacobs/reveal/issues/105) "Can't reveal multiple elements" | Sequences with progress |
| | | "not well tested" on non-Android | **Production KMP focus** |
| [pseudoankit/coachmark](https://github.com/pseudoankit/coachmark) | ~200 | Only rectangles documented | **4 shapes** |
| | | No connector lines | **5 connector styles** |
| [skydoves/Balloon](https://github.com/skydoves/Balloon) | ~4K | Tooltip library, not spotlight | **Purpose-built for coachmarks** |

---

## UX Research Foundation

This library is built on UX research from:

- [Nielsen Norman Group — Instructional Overlays](https://www.nngroup.com/articles/mobile-instructional-overlay/)
- [Nielsen Norman Group — Onboarding Tutorials](https://www.nngroup.com/articles/onboarding-tutorials/)
- [Plotline — Coachmarks improve feature adoption 40-60%](https://www.plotline.so/blog/coachmarks-and-spotlight-ui-mobile-apps)

### Built-in UX Best Practices

| Research Finding | Library Feature |
|------------------|-----------------|
| "One tip at a time" | Single coachmark display, sequences one-by-one |
| "Users skip immediately" | Skip button, easy dismissal |
| "Can't go back is frustrating" | `BackPressBehavior.NAVIGATE` |
| "Repeating is annoying" | `CoachmarkRepository` persistence |
| "Memory is limited" | Short text, progress indicator |
| "Context matters" | `enabled` flag for conditional display |

---

## Contributing

We welcome contributions! Priority areas:

1. **Pulse/Glow Animation** — High impact, relatively contained scope
2. **Skip Tour Button** — Quick win for UX
3. **Accessibility** — Help make coachmarks work for everyone
4. **Platform Testing** — iOS, Desktop, Web via Compose Multiplatform

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

## Release Schedule

| Version | Target | Focus |
|---------|--------|-------|
| v1.0.0 | **This Week** | Foundation, launch |
| v1.1.0 | +2 weeks | Animations, polish |
| v1.2.0 | +4 weeks | Interactions, accessibility |
| v1.3.0 | +8 weeks | DSL, advanced features |
| v2.0.0 | Q2 2026 | Full Compose Multiplatform |

---

## Stay Updated

- **Star the repo** to show support
- **Watch releases** for version notifications
- **Follow [@AditLal](https://twitter.com/anthropic)** for updates

---

*Built with frustration from using other libraries. Made with love for the Compose community.*
