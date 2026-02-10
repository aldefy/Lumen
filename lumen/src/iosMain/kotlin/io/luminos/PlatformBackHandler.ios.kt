package io.luminos

import androidx.compose.runtime.Composable

@Composable
internal actual fun PlatformBackHandler(onBack: () -> Unit) {
    // No-op on iOS: iOS has no system back button.
    // Back navigation is handled by the native navigation controller.
}
