# Tooltip Text Alignment & Inline Title Design

**Date:** 2026-03-05
**Status:** Approved

## Overview

Two related tooltip layout improvements:
1. Add `tooltipTextAlign` parameter to control text alignment (center, start, end)
2. Add `titleInlineWithConnector` option to render the title beside the connector dot on the same horizontal line

## Feature 1: tooltipTextAlign

### API Surface

**CoachmarkConfig** (global default):
```kotlin
data class CoachmarkConfig(
    // ... existing params
    val tooltipTextAlign: TextAlign = TextAlign.Start,
)
```

**CoachmarkTarget** (per-target override):
```kotlin
data class CoachmarkTarget(
    // ... existing params
    val tooltipTextAlign: TextAlign? = null, // null = use CoachmarkConfig value
)
```

### Affected Text Elements

All text in the tooltip column:
- Title (20sp bold)
- Description (14sp)
- Skip button text (14sp) — the Row arrangement changes
- "Don't show again" text (13sp)

**Not affected:** CTA button text (stays centered inside Button).

### Footer Row Behavior

When `textAlign == TextAlign.Center`:
- Footer Row uses `Arrangement.Center` with spacing
- Otherwise keeps `Arrangement.SpaceBetween`

### Propagation Path

`CoachmarkTarget.tooltipTextAlign ?: config.tooltipTextAlign` resolved in `CoachmarkScrim` → passed through `TooltipContainer` → applied in `CoachmarkTooltip`.

## Feature 2: titleInlineWithConnector

### Problem

Current layout stacks the connector dot above the tooltip column:
```
    │ (connector)
    ● (dot)
              ← connectorTooltipGap (8dp)
Muted Call    ← title in Column
Description   ← description
[Got it!]     ← CTA
```

Desired layout places title beside the dot:
```
    │ (connector)
    ●  Muted Call    ← dot and title on same line
       Description   ← description below, indented
       [Got it!]     ← CTA
```

### API Surface

**CoachmarkConfig** (global default):
```kotlin
data class CoachmarkConfig(
    // ... existing params
    val titleInlineWithConnector: Boolean = false,
)
```

**CoachmarkTarget** (per-target override):
```kotlin
data class CoachmarkTarget(
    // ... existing params
    val titleInlineWithConnector: Boolean? = null, // null = use config value
)
```

### Implementation Approach

When `titleInlineWithConnector == true` and connector is VERTICAL:

1. **TooltipContainer refactored:** Instead of a single `CoachmarkTooltip` Column, it composes:
   - An inline `Row` at the connector endpoint Y: `[DotIndicator (composed Box)] [Spacer(4.dp)] [Title Text]`
   - The remaining tooltip body (description, CTA, checkbox) as a Column below

2. **Connector dot moves from Canvas to Compose:** The dot is no longer drawn via `drawConnectorEndpoint()` on canvas. Instead, a small composed `Box` (circle) renders inside the Row. This ensures pixel-perfect alignment with the title baseline.

3. **Connector path adjustment:** The connector `endPointY` calculation already uses `tooltipPosition.y - connectorTooltipGap`. The tooltip position itself shifts down by the inline row height, so the connector naturally ends at the right spot. The dot is suppressed from canvas drawing (treated as `ConnectorEndStyle.NONE` internally) when inline mode is active.

4. **Fallback:** When connector is HORIZONTAL or ELBOW, inline mode is ignored — title renders normally in the Column. The dot stays on canvas.

### Layout Detail

```
┌─ Box (tooltip container, offset-positioned)
│
├─ Row (inline title row)
│  ├─ Box(size=8.dp, circle, filled) ← composed dot
│  ├─ Spacer(width=4.dp)
│  └─ Text(title, 20sp bold)         ← baseline-aligned with dot center
│
├─ Spacer(4.dp)
│
└─ Column (tooltip body, same max width)
   ├─ Text(description)
   ├─ Spacer(16.dp)
   ├─ Row(progress + CTA)
   └─ [Optional checkbox row]
```

### Edge Cases

- **HORIZONTAL/ELBOW connector:** Inline mode disabled, standard layout used
- **No connector (`ConnectorStyle.NONE` equivalent):** Inline mode disabled
- **Card mode (`showTooltipCard = true`):** Inline row sits inside the card. The dot is styled with the card's title color.
- **RTL layouts:** Row handles RTL automatically via Compose layout direction

## Files to Modify

1. **CoachmarkTarget.kt** — Add `tooltipTextAlign: TextAlign?` and `titleInlineWithConnector: Boolean?` to `CoachmarkTarget`. Add `tooltipTextAlign: TextAlign` and `titleInlineWithConnector: Boolean` to `CoachmarkConfig`.
2. **CoachmarkTooltip.kt** — Accept `textAlign` param, apply to all Text composables. Split into inline title Row + body Column when inline mode active.
3. **CoachmarkScrim.kt** — Resolve per-target vs config values. Pass through to `TooltipContainer`. Suppress canvas dot drawing when inline mode active.

## Backward Compatibility

All new params default to current behavior:
- `tooltipTextAlign = TextAlign.Start` (current implicit default)
- `titleInlineWithConnector = false` (current layout)

No breaking changes.
