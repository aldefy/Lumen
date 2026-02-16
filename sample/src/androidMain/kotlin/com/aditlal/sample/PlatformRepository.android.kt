package com.aditlal.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.luminos.CoachmarkRepository

@Composable
internal actual fun rememberCoachmarkRepository(): CoachmarkRepository {
    val context = LocalContext.current
    return remember { CoachmarkRepository(context) }
}
