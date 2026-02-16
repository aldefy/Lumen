package com.aditlal.sample

enum class Example(val title: String, val description: String) {
    BASIC("Basic Coachmark", "Single target with pulse animation"),
    SEQUENCE("Multi-Step Sequence", "5-step tour with progress indicator"),
    SHAPES("Shapes Showcase", "Circle, Rect, RoundedRect, Squircle, Star"),
    CONNECTORS("Connector Styles", "Vertical, Horizontal, Elbow, Direct, Auto"),
    ANIMATIONS("Highlight Animations", "None, Pulse, Glow, Ripple, Shimmer, Bounce"),
    TOOLTIP_POSITION("Tooltip Position", "Top, Bottom, Start, End, Auto"),
    TOOLTIP_OPTIONS("Tooltip Options", "Card wrapper, Skip button, CTA text"),
    THEMING("Theming & Colors", "Custom color schemes"),
    SCRIM_OPACITY("Scrim Opacity", "Light, Medium, Dark, Extra Dark"),
    LAZY_COLUMN("LazyColumn", "Coachmarks in scrollable lists"),
    DIALOG_COORDINATION("Dialog Coordination", "Auto-dismiss when dialogs appear"),
    TAP_THROUGH("Tap-Through Behavior", "Pass-through, Advance, Both tap modes"),
    ANALYTICS("Analytics Callbacks", "Track onShow, onDismiss, onAdvance, onComplete"),
}
