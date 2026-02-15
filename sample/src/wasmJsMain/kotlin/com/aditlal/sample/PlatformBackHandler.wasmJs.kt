package com.aditlal.sample

import androidx.compose.runtime.Composable

@Composable
internal actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-op on Web: browser back navigation is the web app's responsibility.
}
