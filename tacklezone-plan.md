**Here is a high-level, code-agnostic plan to implement Tackle Zone (TZ) rendering in the Java FFB client:**

---

### **Tackle Zone Rendering – Java FFB Client Integration Plan**

#### **1. Define Project Scope and Requirements**

- **Goal:** Render tackle zones as a colored overlay on the pitch, matching the logic and options the JS client.
- **Performance:** Must be fast, responsive, and support all pitch layouts/orientations.
- **Settings:** Use existing settings or add new options for TZ display (alpha, team filters, contour, etc.).

---

#### **2. Architectural Integration**

- **Layer System:**

  - Create a new rendering layer (e.g., `FieldLayerTackleZones`) extending the `FieldLayer` base class.
  - Integrate this layer into `FieldComponent` as part of the rendering stack.
  - Register/init the new layer in the constructor and initialization logic.
  - Update `refresh()` and compositing logic to include the new layer.

---

#### **3. Data Model Access**

- **Players:** Use the `Game` and `FieldModel` to access all players and their coordinates/states.
- **Tile Grid:** Determine the pitch size (width/height) from the model or dimension provider.
- **Tackle Zone Calculation:** For each player with a tackle zone, increment counters in a 2D array representing each tile, separated by team.

---

#### **4. Rendering Logic**

- **Main Rendering Pass:**

  - For each tile, calculate overlap counts for home and away teams.
  - For tiles with overlaps, draw translucent rectangles using Java2D, with color/alpha based on team and overlap count.
  - If both teams overlap a tile, blend by drawing both colors at reduced alpha.

- **Optional:** If settings enabled, draw perimeter/contour lines using edge detection logic (same as JS).

---

#### **5. UI/Settings Integration**

- **Settings Access:**

  - Read user preferences (enable/disable TZ, alpha, show both teams, etc.) from the existing settings infrastructure.
  - Optionally, add new settings in the settings panel and configuration files.

---

#### **6. Update/Redraw Triggers**

- **When to Redraw:**

  - Update the tackle zone overlay any time a player moves, is removed, or their state changes (KO, stun, etc.).
  - Tie into the same model change/observer pattern as existing layers (use observer or event hooks).

---

#### **7. Testing & Debugging**

- **Manual Testing:**

  - Test with all pitch layouts and orientations.
  - Test for overlap, edge cases, and performance on full teams.
  - Verify settings and dynamic updates.

- **Automated:** (optional) Add regression tests or log overlays for debugging.

---

### **Summary Table**

| Step               | Description                                                  |
| ------------------ | ------------------------------------------------------------ |
| 1. Scope           | Define requirements, match JS client, set goals              |
| 2. Architecture    | New `FieldLayerTackleZones`, integrate into `FieldComponent` |
| 3. Model Access    | Access players, positions, states from model                 |
| 4. Render Logic    | Overlay per-tile color by team/overlap, support blending     |
| 5. Settings        | Respect user options for TZ display/alfa/layout              |
| 6. Redraw Triggers | Redraw on player moves/state change, use observer hooks      |
| 7. Testing         | Test all layouts/orientations, performance, correctness      |

---
