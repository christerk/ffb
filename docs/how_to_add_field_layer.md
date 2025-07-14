## FFB Client: Adding a New Field Layer (e.g., Tacklezones Layer)

This guide documents the **required steps and conventions** to integrate a new field rendering layer into the FFB Java client, as done for the `TackleZones` example.

---

### 1. **Create Layer Class**

**Location:** `com.fumbbl.ffb.client.layer`

- **Subclass** `FieldLayer`.
- Implement the drawing logic, property accessors, and update triggers.
- **Example:**

  ```java
  public class FieldLayerTackleZones extends FieldLayer {
    public FieldLayerTackleZones(FantasyFootballClient client, UiDimensionProvider ui, PitchDimensionProvider pitch, FontCache font) {
      super(client, ui, pitch, font);
    }
    @Override
    public void initLayout() {
      super.initLayout();
    }
    @Override
    public void init() {
      updateTackleZones();
    }
    public void updateTackleZones() {
      // ...drawing logic here...
    }
    // ...property accessors, helpers...
  }
  ```

---

### 2. **Add Layer Field in FieldComponent**

**File:** `FieldComponent.java`

- Add a **field** for your new layer (e.g., `private final FieldLayerTackleZones fLayerTackleZones;`).

- Instantiate it in the constructor, passing all required dependencies.

  ```java
  private final FieldLayerTackleZones fLayerTackleZones;
  //...
  fLayerTackleZones = new FieldLayerTackleZones(pClient, uiDimensionProvider, pitchDimensionProvider, fontCache);
  ```

- Provide a public getter:

  ```java
  public FieldLayerTackleZones getLayerTackleZones() {
    return fLayerTackleZones;
  }
  ```

---

### 3. **Integrate Layer in Layout and Init**

- Call `initLayout()` for the new layer in `FieldComponent.initLayout()`.
- Call `init()` for the new layer in `FieldComponent.init()`.

  ```java
  public void initLayout() {
    //...
    fLayerTackleZones.initLayout();
    //...
  }
  public void init() {
    //...
    fLayerTackleZones.init();
    //...
  }
  ```

---

### 4. **Add Layer to Rendering/Compositing**

- **Add the new layer to the composite refresh logic.**

  - Add its updated area to the overall update rectangle list:

    ```java
    Rectangle updatedArea = combineRectangles(new Rectangle[]{
      //...
      getLayerTackleZones().fetchUpdatedArea(),
      //...
    });
    ```

  - Draw the layer at the correct point in the field rendering order:

    ```java
    g2d.drawImage(getLayerTackleZones().getImage(), 0, 0, null);
    ```

---

### 5. **Trigger Layer Update on Model Change**

- In `FieldComponent.update(ModelChange pModelChange)`, call your layer’s update/redraw methods on relevant game state/model changes.

  - Example:

    ```java
    case FIELD_MODEL_SET_PLAYER_COORDINATE:
      getLayerTackleZones().updateTackleZones();
      break;
    case FIELD_MODEL_SET_PLAYER_STATE:
      getLayerTackleZones().updateTackleZones();
      break;
    ```

- **Add any other triggers** as needed (e.g., game state, settings, etc.).

---

### 6. **Layer Registration in Getter/Refresh**

- Make sure your new layer’s getter is included wherever layers are enumerated/accessed (e.g., for test hooks, debugging, overlays).

---

### 7. **(Optional) Property/Setting-Driven Redraw**

- If the layer’s visibility/behavior depends on user settings, implement convenience accessors for those settings (as in your Tacklezone logic).
- Ensure those properties are checked during layer rendering and update appropriately when settings change.

---

## **Summary Table**

| Step | File/Location                     | What to Add                                  |
| ---- | --------------------------------- | -------------------------------------------- |
| 1    | `FieldLayer*.java`                | Create new layer subclass and logic          |
| 2    | `FieldComponent.java` (field)     | Add field, constructor instantiation, getter |
| 3    | `FieldComponent.java` (init)      | Call layer’s `initLayout()` and `init()`     |
| 4    | `FieldComponent.java` (rendering) | Add layer to composite and updated areas     |
| 5    | `FieldComponent.java` (update)    | Trigger layer update on relevant ModelChange |
| 6    | `FieldComponent.java` (access)    | Expose getter, ensure available if needed    |
| 7    | Layer/settings logic (optional)   | Check settings in drawing code               |

---

**Tip:**
Layer order in both the update rectangle and render/compositing sequence determines drawing priority (background ➔ overlays).
Keep this order consistent for correct visuals.
