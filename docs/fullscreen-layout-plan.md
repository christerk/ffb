# Fullscreen And Dynamic Pitch Layout Plan

## Goal

Move the client toward a game-style layout model where the pitch is treated as the world and the Swing GUI is laid out around it. The layout responds to the current client content size, computes the available pitch viewport, and uses a central pitch viewport to convert between pitch/world coordinates and client-content coordinates.

The requested direction is:

- GUI components keep their configured scale unless a resize policy explicitly says otherwise.
- The pitch/world is fitted into the available pitch area while preserving aspect ratio.
- Rendering, input, animation, sketches, and setup drag/drop use the same viewport-derived coordinate model.
- Fullscreen should eventually run the same layout pass as normal resizing.

## Current Status

Phases 1-13 are implemented or partially implemented. Phase 13 is complete. Phases 14 and 15 should be planned together and implemented separately: equal-sized pitch tiles affect the space available to surrounding components.

Phase 12 completed in this branch:

- remove fixed/dynamic layout recipe duplication
- stop using a temporary layout result as a measurement source
- make startup and resize use the same layout path
- keep pitch fitting as one coherent operation that returns bounds and exact scale together
- keep GUI/world separation clear enough for future resize policy work

## Scale Concepts

The layout must distinguish configured scale from runtime resize behavior.

- GUI scale controls non-pitch UI sizes such as sidebars, chat, log, buttons, icons, fonts, timers, and menus.
- Configured pitch scale is the user/configured pitch scale before runtime fitting.
- Runtime pitch fit scale is derived from the available pitch area.
- Effective pitch scale is the configured pitch scale plus any runtime fit scale required by the active resize policy.

The original single `LayoutSettings.scale` concept has started to split into `guiScale`, `pitchScale`, and `dugoutScale`. This is useful, but the naming and ownership still need care because scale policy will become configurable.

## Current Architecture

The current implementation is organized around these boundaries:

```text
LayoutSettings / Component
  -> configured component dimensions

ClientLayoutCalculator
  -> calculates runtime layout from current content size

LayoutAreas
  -> describes major topology regions: rails, pitch area, panel area

PitchViewport
  -> owns pitch bounds and pitch/world coordinate conversion

ClientLayoutResult
  -> carries runtime bounds and pitch scale for one layout pass

ClientLayoutPanel
  -> applies calculated bounds to Swing components

UserInterface
  -> orchestrates resize, layout calculation, viewport updates, and repaint
```

The current viewport uses explicit conversion methods such as `worldToScreen(...)`, `screenToWorld(...)`, `toLocal(...)`, and `toFieldCoordinate(...)`. It does not currently expose `AffineTransform` instances directly. That remains a possible later cleanup if it simplifies rendering/input consistency.

## Current Runtime Flow

The intended runtime flow is one-way:

```text
settings + current content size
  -> configured component dimensions
  -> major layout areas
  -> pitch fit
  -> final panel placement
  -> ClientLayoutResult
  -> PitchViewport + Swing component bounds
```

Rules:

- Runtime layout results must not mutate settings.
- Viewports must not make layout decisions.
- Components must not infer runtime layout from preferred sizes.
- Exact pitch scale must be produced by pitch fitting, not reverse-engineered from rounded pitch bounds.

## Phase 1: Centralize Pitch Transform

Status: Done.

Implemented as `PitchViewport`.

The viewport centralizes field-local mapping, pitch/world coordinate conversion, orientation handling, direction remapping, runtime pitch bounds, and effective pitch scale.

## Phase 2: Add Mapping Tests

Status: Done / partial.

Focused tests exist for viewport and coordinate behavior. More regression coverage can be added as rough edges are found, but this phase is no longer blocking the layout work.

## Phase 3: Route Input Through The Viewport

Status: Done.

`CoordinateConverter` delegates to `PitchViewport`.

## Phase 4: Route Common Rendering Through The Viewport

Status: Done / partial.

Rendering code has been moved toward `PitchViewport` and `PitchDimensionProvider` now delegates effective pitch scale to the viewport. Some rendering code still uses local helper methods and rounded square-size behavior. Fractional square boundaries are handled later.

## Phase 5: Introduce A Layout Result

Status: Done.

`ClientLayoutResult` now represents the output of one runtime layout pass. It carries content size, pitch bounds, GUI component bounds, and pitch scale.

Note: `runtimeGuiScale()` is carried for the upcoming resize-policy step, where GUI scale may become runtime policy output.

## Phase 6: Make UserInterface Use The Layout Panel

Status: Done.

`ClientLayoutPanel` owns applying calculated bounds to Swing components. `UserInterface` orchestrates component initialization, layout calculation, viewport updates, and repaint.

## Phase 7: Promote Local Mapping To Viewport Transforms

Status: Done / partial.

`PitchViewport` exposes `worldToScreen(...)` and `screenToWorld(...)` for client-content coordinates. It also keeps local mapping methods for rendering code that still works inside the field component or field layer buffers.

## Phase 8: Handle Setup Drag/Drop Coordinate Mapping

Status: Done / partial.

`ReserveBoxViewport` and `SetupDragHitTester` separate reserve/box hit testing from pitch/world mapping. This keeps setup drag behavior out of `PitchViewport`.

## Phase 9: Split GUI Scale From Pitch Scale

Status: Done / partial.

`LayoutSettings` now has separate GUI, pitch, and dugout scales. This is an important step, but it is not the final resize-policy design.

## Phase 10: Make The Window Resizable

Status: Done.

The frame is resizable and resize events recompute the layout result, update the pitch viewport, update component bounds, and redraw the field.

## Phase 11: Fit Pitch To Available Space

Status: Done / being refined.

Dynamic pitch fitting exists. The pitch fit produces both:

- integer pitch bounds
- exact runtime pitch scale

Current visual behavior:

- bottom-panel layouts top-align the fitted pitch inside the pitch area and place score/log/chat after the fitted pitch
- right-panel layouts keep the panels in the right-side panel area

This replaces the older wording that simply said to center the pitch inside the available area. Centering is not the rule for every topology.

## Phase 12: Normalize Layout Data Flow

Status: Done.

The goal of this phase is to make fixed startup layout and dynamic resize layout the same calculation.

Important design point:

```text
There should not be separate fixed and dynamic rectangle recipes.
```

The old fixed layout is just the result of running the same runtime layout flow with the configured/natural content size.

Implemented flow:

```text
LayoutSettings + available content size
  -> resolve component dimensions
  -> LayoutAreas.arrange(...)
  -> fit pitch into pitch area
  -> place GUI panels
  -> ClientLayoutResult
```

`LayoutAreas` should own major topology only:

- home rail
- away rail
- pitch area
- panel area
- the rule that bottom panels follow the fitted pitch

`ClientLayoutCalculator` should own:

- dimension resolution
- pitch fitting
- final result assembly
- local score/log/chat panel placement

Remaining note: score/log/chat placement still contains local rectangle arithmetic. That is acceptable for now because it is contained and named by intent.

## Phase 13: Add Configurable Resize Scaling Policy

Status: Done.

The client offers an overall scale and a GUI scale control. Changing overall scale resizes the current window proportionally; changing GUI scale resizes the composed layout around the current pitch scale. The GUI slider ranges from 25% to 100%; larger interface elements require a larger overall scale or future layout changes. Manual window resizing scales the complete layout while preserving its proportions. If the window has a different aspect ratio, the layout is centered in the available space.

The earlier on/off resize policy has been removed. The reference layout uses the selected GUI scale so it does not reserve space for larger interface components.

## Phase 14: Equal-Sized Pitch Tiles

Status: In progress; interaction and visual verification remain.

The goal is for every pitch tile to have the same whole-pixel width and height. Fractional boundaries alone cannot guarantee equal rasterized tile sizes: adjacent tiles can differ by a pixel.

Plan this phase together with phase 15, but implement them separately. Investigate fitting the largest integer tile size into the available pitch area, then derive pitch bounds and effective scale from that size. Resizing would make the pitch grow in tile-size steps and can leave unused space until phase 15 handles placement. Confirm the approach against the existing two-pixel field allowance, pitch artwork, wide layout scale, and minimum window sizes before implementation.

The pitch artwork, overlays, clearing and repaint regions, normal input, and setup drag hit testing must agree on the same grid in both orientations. Define edge and border behavior explicitly. Keep pitch fitting in the layout calculation and coordinate mapping in the viewport; avoid coupling the viewport to GUI placement.

Verify integer and fractional window sizes, both orientations, tile boundaries and adjacent input pixels, setup dragging, redraws, and artwork alignment. Do not add a change list entry for phase 14.

## Phase 15: Move GUI To Rule-Based Placement

Status: Future.

Move non-pitch GUI components toward rule-based placement:

- sidebars pinned to left/right when wide enough
- log/chat placed bottom or side depending on available shape
- score/status/timer components pinned to top or bottom
- pitch takes the remaining available area

Phase 14's integer tile fit may leave space around the pitch. Plan how to use that space per layout without assuming every component can stretch. The current landscape and wide layouts place information below the central pitch area; portrait places it below the game area; square places it to the right. Consider both axes for each topology, including moving rails and expanding log/chat containers without scaling their contents. Decide which gaps are acceptable and account for different window aspect ratios, minimum sizes, and existing component constraints. Do not promise a gap-free result until these rules are validated.

This is where the client can start moving away from fixed `ClientLayout` variants.

## Phase 16: Fullscreen

Status: Future.

Once resizing works, fullscreen should mostly be:

- switch Java window/display mode
- get available screen bounds
- run the same layout pass
- update the same pitch viewport
- redraw

No special pitch coordinate logic should be needed for fullscreen.

## Known Rough Edges

- `LayoutSettings` scale naming is still confusing because GUI, pitch, and dugout scale are now separate but the final resize-policy API is not done.
- `ClientLayoutResult.runtimeGuiScale()` is present for the upcoming resize-policy work.
- `PitchViewport` manually implements transform behavior instead of exposing `AffineTransform` objects.
- `ClientLayoutCalculator` still has local rectangle arithmetic for score/log/chat placement.
- The branch is broad because layout, coordinate conversion, rendering, input, animation, dialogs, and setup drag all depend on pitch position/scale.
