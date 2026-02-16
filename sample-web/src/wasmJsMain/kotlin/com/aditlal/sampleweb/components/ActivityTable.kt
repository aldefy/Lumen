package com.aditlal.sampleweb.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkController
import io.luminos.coachmarkTarget

private data class ActivityEvent(
    val user: String,
    val action: String,
    val time: String,
    val color: Color,
)

private val events = listOf(
    ActivityEvent("Alice", "Completed onboarding tour", "2m ago", Color(0xFF10B981)),
    ActivityEvent("Bob", "Skipped step 3", "5m ago", Color(0xFFF59E0B)),
    ActivityEvent("Carol", "Viewed analytics page", "12m ago", Color(0xFF6366F1)),
    ActivityEvent("Dave", "Started feature tour", "18m ago", Color(0xFF10B981)),
    ActivityEvent("Eve", "Dismissed coachmark", "25m ago", Color(0xFFEF4444)),
)

@Composable
fun ActivityTable(
    controller: CoachmarkController,
    onStartTour: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .coachmarkTarget(controller, "activity_table")
            .padding(20.dp),
    ) {
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(Modifier.height(16.dp))

        events.forEach { event ->
            ActivityRow(event)
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(8.dp))

        FloatingActionButton(
            onClick = onStartTour,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.End)
                .coachmarkTarget(controller, "start_tour_fab"),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "Start Tour",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun ActivityRow(event: ActivityEvent) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(event.color),
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = event.user,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = event.action,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = event.time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
