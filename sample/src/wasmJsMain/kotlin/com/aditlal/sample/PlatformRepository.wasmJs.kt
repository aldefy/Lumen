package com.aditlal.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.luminos.CoachmarkRepository

@Composable
internal actual fun rememberCoachmarkRepository(): CoachmarkRepository {
    return remember { CoachmarkRepository() }
}
