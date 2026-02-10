package com.aditlal.sample

import androidx.compose.runtime.Composable

@Composable
internal actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-op: iOS has no system back button.
    // Each example has a TopAppBar back arrow for navigation.
}
