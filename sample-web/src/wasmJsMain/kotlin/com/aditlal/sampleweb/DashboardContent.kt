package com.aditlal.sampleweb

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aditlal.sampleweb.components.ActivityTable
import com.aditlal.sampleweb.components.ChartCard
import com.aditlal.sampleweb.components.MetricCards
import com.aditlal.sampleweb.components.Sidebar
import com.aditlal.sampleweb.components.TopBar
import io.luminos.CoachmarkController

@Composable
fun DashboardContent(
    controller: CoachmarkController,
    onStartTour: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Sidebar(controller = controller)

        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            TopBar(controller = controller)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
            ) {
                MetricCards(controller = controller)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                ) {
                    ChartCard(
                        controller = controller,
                        modifier = Modifier.weight(1f),
                    )

                    ActivityTable(
                        controller = controller,
                        onStartTour = onStartTour,
                        modifier = Modifier.weight(1f).padding(start = 24.dp),
                    )
                }
            }
        }
    }
}
