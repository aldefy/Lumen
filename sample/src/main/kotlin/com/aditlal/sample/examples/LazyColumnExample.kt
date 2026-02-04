package com.aditlal.sample.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkConfig
import io.luminos.CoachmarkHost
import io.luminos.CoachmarkTarget
import io.luminos.ConnectorStyle
import io.luminos.CutoutShape
import io.luminos.HighlightAnimation
import io.luminos.ScrimTapBehavior
import io.luminos.coachmarkColors
import io.luminos.coachmarkTarget
import io.luminos.rememberCoachmarkController

private data class ListItem(
    val id: Int,
    val title: String,
    val subtitle: String,
)

private val sampleItems = (1..20).map { index ->
    ListItem(
        id = index,
        title = "Item $index",
        subtitle = "Description for item $index with some additional text",
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyColumnExample(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val controller = rememberCoachmarkController()
    var showSequence by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    // Wire up scroll state to controller for visibility tracking
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .collect { isScrolling ->
                controller.isScrolling = isScrolling
            }
    }

    CoachmarkHost(
        controller = controller,
        config = CoachmarkConfig(
            highlightAnimation = HighlightAnimation.PULSE,
            showSkipButton = true,
            skipButtonText = "Skip tour",
            scrimTapBehavior = ScrimTapBehavior.NONE,
        ),
        colors = coachmarkColors(),
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text("LazyColumn Demo") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.coachmarkTarget(controller, "more_options")
                        ) {
                            Icon(Icons.Default.MoreVert, "More")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                // Header with start button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Coachmarks in Scrollable Lists",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Targets are tracked even when scrolling",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { showSequence = true }) {
                        Text("Start Tour")
                    }
                }

                // Scrollable list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(sampleItems) { index, item ->
                        ListItemCard(
                            item = item,
                            controller = controller,
                            // Target specific items for coachmarks
                            isFavoriteTarget = index == 0,
                            isDeleteTarget = index == 2,
                            isCardTarget = index == 4,
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showSequence) {
        controller.showSequence(
            listOf(
                CoachmarkTarget(
                    id = "more_options",
                    title = "More Options",
                    description = "Access additional settings and bulk actions for the list from here.",
                    shape = CutoutShape.Circle(radiusPadding = 8.dp),
                    connectorStyle = ConnectorStyle.ELBOW,
                    connectorLength = 80.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "favorite_button",
                    title = "Favorite Items",
                    description = "Tap the heart icon to add items to your favorites for quick access later.",
                    shape = CutoutShape.Circle(radiusPadding = 8.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 70.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "delete_button",
                    title = "Delete Items",
                    description = "Remove items from the list by tapping the delete icon. This action can be undone.",
                    shape = CutoutShape.Circle(radiusPadding = 8.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 70.dp,
                    ctaText = "Next",
                ),
                CoachmarkTarget(
                    id = "list_card",
                    title = "Item Cards",
                    description = "Tap any card to view details. Long press to select multiple items.",
                    shape = CutoutShape.RoundedRect(cornerRadius = 12.dp, padding = 4.dp),
                    connectorStyle = ConnectorStyle.VERTICAL,
                    connectorLength = 80.dp,
                    ctaText = "Got it!",
                ),
            )
        )
        showSequence = false
    }
}

@Composable
private fun ListItemCard(
    item: ListItem,
    controller: io.luminos.CoachmarkController,
    isFavoriteTarget: Boolean,
    isDeleteTarget: Boolean,
    isCardTarget: Boolean,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .then(
                if (isCardTarget) {
                    Modifier.coachmarkTarget(controller, "list_card")
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.id.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }

            // Action buttons
            IconButton(
                onClick = { },
                modifier = if (isFavoriteTarget) {
                    Modifier.coachmarkTarget(controller, "favorite_button")
                } else {
                    Modifier
                }
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(
                onClick = { },
                modifier = if (isDeleteTarget) {
                    Modifier.coachmarkTarget(controller, "delete_button")
                } else {
                    Modifier
                }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
