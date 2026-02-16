package com.aditlal.sampleweb.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkController
import io.luminos.coachmarkTarget

private data class Metric(
    val label: String,
    val value: String,
    val change: String,
    val positive: Boolean,
)

private val metrics = listOf(
    Metric("Total Users", "12,847", "+12.5%", true),
    Metric("Sessions", "1,024", "+8.2%", true),
    Metric("Events", "48,392", "+23.1%", true),
    Metric("Conversion", "3.2%", "-0.4%", false),
)

@Composable
fun MetricCards(controller: CoachmarkController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        metrics.forEachIndexed { index, metric ->
            val modifier = if (index == 0) {
                Modifier.weight(1f).coachmarkTarget(controller, "metric_card")
            } else {
                Modifier.weight(1f)
            }
            MetricCard(metric = metric, modifier = modifier)
        }
    }
}

@Composable
private fun MetricCard(metric: Metric, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp),
    ) {
        Text(
            text = metric.label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = metric.value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = metric.change,
            style = MaterialTheme.typography.bodySmall,
            color = if (metric.positive) Color(0xFF10B981) else Color(0xFFEF4444),
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
