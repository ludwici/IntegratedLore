# IntegratedLore

Interactive angle-based item inspection for Minecraft 1.21.1 (NeoForge), with layouts powered by [AnchorUI](https://github.com/ludwici/AnchorUI).

## Prototype features

- Hold the **left mouse button** over the center item and drag to rotate it.
- Unlock clues by viewing the item at specific yaw/pitch orientations.
- Discovered clues are listed in a scrollable journal on the left and stay unlocked until the inspection screen closes.
- Custom AnchorUI widget types: `integratedlore:viewer` and `integratedlore:journal`.
- Panels, labels, responsive anchors, widget placement and clue definitions live in AnchorUI JSON documents; resource packs can override these.
- The first implementation uses Minecraft GUI item models and orientation thresholds, not mesh raycasts. Flat item icons do not gain true depth.

## Try it in-game

Requires Java 21, NeoForge 21.1.170, Minecraft 1.21.1 and the AnchorUI development build from its `1.21.1` branch.

- `/integratedlore demo`: inspect a crafting table with three clues.
- `/integratedlore inspect`: inspect the item held in the main hand; the crafting table has an authored layout, all other items currently use the generic viewer.
- `F8`: open the AnchorUI screen editor; `Esc`: close the inspection.

## Build

Clone both repositories as sibling directories named `AnchorUI` and `IntegratedLore`. Checkout AnchorUI's `1.21.1` branch, then use **Gradle 8.8** with JDK 21 to run `gradle build` inside `IntegratedLore`.

Gradle includes the sibling AnchorUI as a composite build and substitutes `com.ludwici:anchorui:1.0.0`. CI checks out AnchorUI at commit `86a12f90eb0fe4e2caef17385ac70579bbcd0c5d` to ensure reproducibility. A locally published AnchorUI Maven artifact is an alternative if the sibling directory is absent.

## Author an inspection

Supply a standard AnchorUI screen at `assets/integratedlore/anchorui/screens/<namespace>_<item_path>.json`. For `minecraft:crafting_table` the resource is `screens/minecraft_crafting_table.json`. Documents are picked up through AnchorUI's resource reload system, including resource packs.

The layout can include regular `anchorui:panel`, `anchorui:text`, and other widgets, plus root-level `integratedlore:journal` and `integratedlore:viewer`. The viewer's `clues` array defines observations. Example:

```json
{
  "type": "integratedlore:viewer",
  "id": "viewer",
  "clues": [
    {
      "id": "inscription",
      "title": "Unusual inscription",
      "description": "A mark is hidden on the object's reverse side.",
      "yaw": 110,
      "pitch": 10,
      "tolerance": 15,
      "markerX": 0.2,
      "markerY": -0.1
    }
  ]
}
```

Yaw and pitch are degrees: initial yaw is 25, initial pitch is 15, yaw wraps through 180, and pitch is clamped to -85..85. Tolerance is an angular threshold on both axes; markerX / markerY are normalized display positions (-1..1). The clue automatically unlocks once the view matches, and remains in the journal until the screen is closed.

## Future work

True item geometry for flat icons, raycast/model-surface hotspot picking, persistence (server authoritative where needed), UI polish, localization, and in-game GUI-scale/resource-reload testing.
