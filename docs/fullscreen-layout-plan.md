# Fullscreen And Dynamic Pitch Layout Plan

## Goal

Move the client toward a game-style layout model where the pitch is treated as the world and the Swing GUI is laid out around it. The layout should eventually respond to the current window size, compute the available pitch viewport, and use explicit transforms to convert between pitch/world coordinates and screen/UI coordinates.

The first phase should preserve current behavior. Fullscreen and fully dynamic resizing should come later, after coordinate conversion and pitch rendering are centralized.

## Scale Concepts

The dynamic layout should separate pitch scaling from GUI scaling.

- Pitch scale should be derived from the available pitch viewport and the fixed world size of the pitch.
- GUI scale should be a user setting that controls non-pitch UI sizes such as sidebars, chat, log, buttons, icons, fonts, timers, and menus.

This allows users on large high-resolution screens to make the GUI larger without forcing the pitch transform to use the same scale. The pitch should still fit the available area while preserving its aspect ratio.

The current `LayoutSettings.scale` is applied by `DimensionProvider` across UI, pitch, and dugout render contexts. Pitch and dugout sizing also apply layout-specific multipliers via `ClientLayout.getPitchScale()` and `ClientLayout.getDugoutScale()`. During the refactor, this may need to become more explicit, for example:

- `guiScale` for non-pitch components
- viewport-derived pitch scale for the pitch/world transform

Existing scale behavior should be preserved first, then split once the layout/viewport responsibilities are clear.

## Current Foundation

- `FieldCoordinate` already represents field grid coordinates, and also encodes box/dugout positions through sentinel x values.
- `PitchDimensionProvider` already handles much of the field-to-pixel mapping.
- `CoordinateConverter` already handles normal field mouse-to-field conversion.
- Setup drag/drop has additional coordinate conversion in `UtilClientPlayerDrag` because it spans the field and box/dugout UI.
- `FieldLayer` and several field layer subclasses use `PitchDimensionProvider.mapToLocal(...)` for rendering.
- `UserInterface` currently builds fixed layouts using `ClientLayout`, `Component`, `LayoutSettings`, `BoxLayout`, and `pack()`.

The main issue is that transform logic exists, but it is spread across multiple classes rather than being represented as one explicit viewport/camera-style abstraction.

## Proposed Direction

Introduce a pitch viewport/transform abstraction that initially centralizes current field-local coordinate behavior, and later owns:

- the pitch viewport rectangle in UI/screen space
- world-to-screen conversion
- screen-to-world conversion
- square/tile bounds
- scale
- orientation handling
- direction remapping where needed

Possible names:

- `PitchViewport`
- `PitchTransform`
- `PitchCamera`

`PitchViewport` is probably the clearest name if the class owns both the screen rectangle and the transforms.

## Phase 1: Centralize Pitch Transform

Add a new client-side abstraction that reproduces the current behavior:

```java
public class PitchViewport {
  public Dimension fieldSize();
  public int squareSize();
  public int imageOffset();
  public Dimension toLocal(FieldCoordinate coordinate);
  public Dimension toLocal(FieldCoordinate coordinate, boolean center);
  public FieldCoordinate toFieldCoordinate(Point localPoint);
  public Direction toLocal(Direction direction);
}
```

This first API should use current-code terminology and preserve existing behavior. `worldToScreen` / `screenToWorld` terminology comes later, once the viewport owns screen bounds.

At this stage it can still use existing `LayoutSettings`, `ClientLayout`, and fixed component sizes internally. The purpose is to move responsibility first, not change behavior.

## Phase 2: Add Mapping Tests

Add focused tests before changing rendering behavior:

- landscape field coordinate to screen coordinate
- portrait field coordinate to screen coordinate
- screen coordinate to field coordinate
- portrait direction remapping
- bounds/edge behavior

These tests should lock down existing behavior so later dynamic layout changes are safer.

## Phase 3: Route Input Through The Viewport

Update `CoordinateConverter` so it delegates to `PitchViewport.toFieldCoordinate(...)`.

This is a low-risk first migration because normal field mouse handling already goes through `CoordinateConverter`.

Special drag/drop behavior in `UtilClientPlayerDrag` is intentionally excluded from this initial input migration. It has different edge behavior from `CoordinateConverter`, handles field/box crossing, and can produce reserve/box sentinel coordinates. It is handled separately after the layout result and viewport bounds exist.

## Phase 4: Route Common Rendering Through The Viewport

Update common `FieldLayer` helpers to use `PitchViewport.toLocal(...)` and `PitchViewport.squareSize()`.

Then gradually migrate direct `pitchDimensionProvider.mapToLocal(...)` calls in individual field layers, states, animations, sketches, range rulers, and marker popup placement.

This phase covers rendering and UI placement coordinate mapping. Setup drag/drop input is handled separately after layout bounds exist.

## Phase 5: Introduce A Layout Result

The layout responsibilities should be split like this:

```text
Component enum / UiDimensionProvider
  gives fixed component sizes

ClientLayoutCalculator
  places those sizes into a window layout

ClientLayoutResult
  stores the resulting final component bounds

ClientLayoutPanel
  applies those bounds to Swing components

UserInterface / PitchViewport
  consume the layout result at their own boundaries
```

At first the calculator should reproduce the current fixed layout. Later phases can change the calculator to use the available window size, GUI scale, and pitch aspect ratio without spreading that logic through `UserInterface`.

Add a small object that represents the result of a client layout pass:

```java
public class ClientLayoutResult {
  public Rectangle fieldBounds;
  public Rectangle homeSidebarBounds;
  public Rectangle awaySidebarBounds;
  public Rectangle scoreBarBounds;
  public Rectangle logBounds;
  public Rectangle chatBounds;
}
```

This example is not exhaustive. It should initially include the fixed content preferred size and final bounds for the real client components:

- field
- home sidebar
- away sidebar
- score bar
- log
- chat

It should include any additional replay/status/menu areas later if they become part of the content layout.

Initially this result can reproduce the current fixed `LANDSCAPE`, `PORTRAIT`, `SQUARE`, and `WIDE` layouts.

The layout pass should take GUI scale into account when calculating non-pitch component sizes. The pitch viewport should then be calculated from whatever space remains.

The important shift is that layout becomes data calculated by one component, instead of being implicit in nested Swing panels.

## Phase 6: Make UserInterface Use The Layout Panel

Refactor `UserInterface.initComponents(...)` so it calculates a `ClientLayoutResult` and uses `ClientLayoutPanel` as the fixed content panel.

Initial behavior should remain visually identical:

- same packed window size
- same component sizes
- same layout variants
- same non-resizable behavior
- same `pack()` behavior

`ClientLayoutPanel` owns applying the calculated bounds to Swing components. `UserInterface` should orchestrate component initialization, layout calculation, adding the content panel to the desktop, and `pack()`.

This creates a bridge from the current fixed model to a dynamic layout pass.

`PitchViewport` bounds, resizing, and `worldToScreen` / `screenToWorld` terminology remain outside this phase.

## Phase 7: Promote Local Mapping To Viewport Transforms

After the layout result exists and `PitchViewport` owns the calculated pitch bounds, introduce viewport terminology:

- `worldToScreen(FieldCoordinate)`
- `screenToWorld(Point)`

Keep `toLocal(...)` and `toFieldCoordinate(...)` as field-component-local methods. They remain correct for rendering and input code whose coordinate parent is the `FieldComponent` or a field layer buffer.

Use `worldToScreen(...)` and `screenToWorld(...)` for coordinates in the client content / viewport space, where the pitch position inside `ClientLayoutPanel` matters. In this plan, "screen" means the client content coordinate space, not OS/global monitor coordinates.

Caller migration should follow the coordinate space involved. Code that works in client content coordinates can use the viewport transform methods, while field-local rendering can continue to use the local methods.

## Phase 8: Handle Setup Drag/Drop Coordinate Mapping

Evaluate `UtilClientPlayerDrag` after the layout result exists and `PitchViewport` owns calculated pitch bounds. Setup drag/drop has different edge behavior from `CoordinateConverter`, handles field/box crossing, and can produce reserve/box sentinel coordinates.

The current behavior should be pinned with tests before changing it:

- field edge coordinates
- portrait conversion
- box-to-field dragging
- field-to-box dragging
- reserve/box sentinel coordinates

Then design a layout-aware setup drag/drop mapper. This should probably be separate from `PitchViewport`: the viewport should own pitch coordinate transforms, while setup drag/drop hit testing should interpret pitch, box, and crossing behavior for the current interaction mode.

Setup drag/drop spans more than one region. `PitchViewport` can represent the pitch area, but reserve/box interaction has its own grid, title offset, edge behavior, and sentinel `FieldCoordinate` values. A future box/dugout viewport may make sense once those bounds are represented explicitly. Until then, setup drag/drop should keep that interpretation in a setup-specific mapper rather than adding reserve/box rules to `PitchViewport`.

## Phase 9: Split GUI Scale From Pitch Scale

Separate the existing broad `LayoutSettings.scale` behavior into clearer concepts:

- GUI scale for non-pitch components, fonts, icons, sidebars, chat, log, timers, and menus
- pitch scale derived from the pitch viewport size

Initially, preserve current behavior by applying the existing scale value as GUI scale and letting the fixed pitch viewport reproduce the old pitch size.

Once dynamic layout is active, GUI scale should affect how much space non-pitch UI components request. Pitch scale should be calculated from the remaining pitch viewport and should preserve the pitch aspect ratio.

## Phase 10: Make The Window Resizable

Once the viewport and layout result exist, allow the frame to resize.

On resize:

- read the current content size
- recompute the layout result
- update component bounds
- update the pitch viewport transform
- recreate/redraw field layer buffers as needed

This is the first point where the new architecture is tested against real changing window sizes.

## Phase 11: Fit Pitch To Available Space

Implement the dynamic pitch sizing logic:

```text
available pitch area = remaining rectangle after GUI placement
scale = min(availableWidth / 26, availableHeight / 15)
pitchWidth = 26 * scale
pitchHeight = 15 * scale
center pitch inside available area
```

The pitch viewport should generate `AffineTransform` instances for:

- `worldToScreen`
- `screenToWorld`

The pitch viewport should update these transforms from the calculated field bounds. Tile boundaries should come from the transform rather than repeated integer square-size calculations.

This pitch scale should be independent from GUI scale. GUI scale affects how much space the non-pitch components need; pitch scale is then derived from the remaining viewport.

## Phase 12: Move GUI To Rule-Based Placement

Move non-pitch GUI components toward rule-based placement:

- sidebars pinned to left/right when wide enough
- log/chat placed bottom or side depending on available shape
- score/status/timer components pinned to top or bottom
- pitch takes the remaining available area

This is where the client starts moving away from fixed `ClientLayout` variants.

## Phase 13: Fullscreen

Once resizing works, fullscreen should mostly be:

- switch Java window/display mode
- get available screen bounds
- run the same layout pass
- update the same pitch viewport
- redraw

No special pitch coordinate logic should be needed for fullscreen.
