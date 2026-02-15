package io.luminos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent

@Composable
internal actual fun PlatformBackHandler(onBack: () -> Unit) {
    DisposableEffect(onBack) {
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
