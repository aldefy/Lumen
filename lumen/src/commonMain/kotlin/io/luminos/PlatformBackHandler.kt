package io.luminos

import androidx.compose.runtime.Composable

@Composable
internal expect fun PlatformBackHandler(onBack: () -> Unit)
