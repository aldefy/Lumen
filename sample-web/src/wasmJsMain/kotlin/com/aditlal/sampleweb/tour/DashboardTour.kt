package com.aditlal.sampleweb.tour

import androidx.compose.ui.unit.dp
import io.luminos.CoachmarkTarget
import io.luminos.ConnectorStyle
import io.luminos.CutoutShape
import io.luminos.HighlightAnimation
import io.luminos.TooltipPosition

fun dashboardTourTargets(): List<CoachmarkTarget> = listOf(
    CoachmarkTarget(
        id = "sidebar",
        title = "Navigation",
        description = "Browse different sections of your analytics dashboard from the sidebar.",
        shape = CutoutShape.RoundedRect(cornerRadius = 12.dp, padding = 4.dp),
        highlightAnimation = HighlightAnimation.GLOW,
        connectorStyle = ConnectorStyle.AUTO,
        tooltipPosition = TooltipPosition.AUTO,
        ctaText = "Next",
    ),
    CoachmarkTarget(
        id = "search",
        title = "Search",
        description = "Quickly find users, events, or metrics across your entire dashboard.",
        shape = CutoutShape.Circle(),
        highlightAnimation = HighlightAnimation.PULSE,
        connectorStyle = ConnectorStyle.AUTO,
        tooltipPosition = TooltipPosition.AUTO,
        ctaText = "Next",
    ),
    CoachmarkTarget(
        id = "notifications",
        title = "Notifications",
        description = "Stay updated with real-time alerts about user activity and tour completions.",
        shape = CutoutShape.Circle(),
        highlightAnimation = HighlightAnimation.BOUNCE,
        connectorStyle = ConnectorStyle.AUTO,
        tooltipPosition = TooltipPosition.AUTO,
        ctaText = "Next",
    ),
    CoachmarkTarget(
        id = "metric_card",
        title = "Key Metrics",
        description = "Track your most important KPIs at a glance — users, sessions, events, and conversion rates.",
        shape = CutoutShape.RoundedRect(cornerRadius = 12.dp),
        highlightAnimation = HighlightAnimation.PULSE,
        connectorStyle = ConnectorStyle.AUTO,
        tooltipPosition = TooltipPosition.AUTO,
        ctaText = "Next",
    ),
    CoachmarkTarget(
        id = "chart_card",
        title = "Weekly Activity",
        description = "Visualize trends over time. This chart shows daily event counts for the past week.",
        shape = CutoutShape.RoundedRect(cornerRadius = 12.dp),
        highlightAnimation = HighlightAnimation.GLOW,
        connectorStyle = ConnectorStyle.AUTO,
        tooltipPosition = TooltipPosition.AUTO,
        ctaText = "Next",
    ),
    CoachmarkTarget(
        id = "activity_table",
        title = "Recent Activity",
        description = "See a live feed of user interactions — who completed tours, skipped steps, or dismissed coachmarks.",
        shape = CutoutShape.Squircle(cornerRadius = 20.dp),
        highlightAnimation = HighlightAnimation.RIPPLE,
        connectorStyle = ConnectorStyle.AUTO,
        tooltipPosition = TooltipPosition.AUTO,
        ctaText = "Next",
    ),
    CoachmarkTarget(
        id = "start_tour_fab",
        title = "Replay Tour",
        description = "You can restart this feature tour anytime by tapping this button. Enjoy exploring!",
        shape = CutoutShape.Circle(),
        highlightAnimation = HighlightAnimation.PULSE,
        connectorStyle = ConnectorStyle.AUTO,
        tooltipPosition = TooltipPosition.AUTO,
        ctaText = "Done!",
    ),
)
