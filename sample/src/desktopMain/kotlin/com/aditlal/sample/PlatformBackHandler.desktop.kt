package com.aditlal.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent

@Composable
internal actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    DisposableEffect(enabled, onBack) {
        if (!enabled) return@DisposableEffect onDispose {}
        val dispatcher = KeyEventDispatcher { event ->
            if (event.id == KeyEvent.KEY_PRESSED && event.keyCode == KeyEvent.VK_ESCAPE) {
                onBack()
                true
            } else {
                false
            }
        }
        val focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager()
        focusManager.addKeyEventDispatcher(dispatcher)
        onDispose {
            focusManager.removeKeyEventDispatcher(dispatcher)
        }
    }
}
