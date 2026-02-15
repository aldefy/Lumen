package io.luminos

import androidx.compose.runtime.Composable

@Composable
internal actual fun PlatformBackHandler(onBack: () -> Unit) {
    // No-op on Web: browser back navigation is the web app's responsibility.
}
