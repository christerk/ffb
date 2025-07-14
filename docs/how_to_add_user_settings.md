## FFB Client: Adding New User Settings (Tacklezones Example)

This guide documents the exact steps and conventions used to add new user settings to the FFB Java client, using the Tacklezones feature as an example.

---

### 1. **Define Setting Keys** (`CommonProperty.java`)

- **Purpose:** Add unique string keys for each setting.
- **Location:** `CommonProperty.java`
- **Example:**

  ```java
  public static final String SETTING_TACKLEZONES_HOME = "setting.tacklezones.home";
  public static final String SETTING_TACKLEZONES_AWAY = "setting.tacklezones.away";
  public static final String SETTING_TACKLEZONES_OPPOSING = "setting.tacklezones.opposing";
  public static final String SETTING_TACKLEZONES_NO_OVERLAP = "setting.tacklezones.noOverlap";
  public static final String SETTING_TACKLEZONES_CONTOUR = "setting.tacklezones.contour";
  ```

---

### 2. **Define Setting Values** (`IClientPropertyValue.java`)

- **Purpose:** Enumerate all possible states for each setting (e.g., ON/OFF).
- **Location:** `IClientPropertyValue.java`
- **Example:**

  ```java
  String SETTING_TACKLEZONES_HOME_ON = "homeTzOn";
  String SETTING_TACKLEZONES_HOME_OFF = "homeTzOff";
  String SETTING_TACKLEZONES_AWAY_ON = "awayTzOn";
  String SETTING_TACKLEZONES_AWAY_OFF = "awayTzOff";
  String SETTING_TACKLEZONES_OPPOSING_ON = "opposingTzOn";
  String SETTING_TACKLEZONES_OPPOSING_OFF = "opposingTzOff";
  String SETTING_TACKLEZONES_NO_OVERLAP_ON = "noOverlapTzOn";
  String SETTING_TACKLEZONES_NO_OVERLAP_OFF = "noOverlapTzOff";
  String SETTING_TACKLEZONES_CONTOUR_ON = "tzContourOn";
  String SETTING_TACKLEZONES_CONTOUR_OFF = "tzContourOff";
  ```

---

### 3. **Create Menu UI** (`GameMenuBar.java`)

- **Purpose:** Add new menu items for each setting (ON/OFF toggle), group by feature.
- **Implementation:**

  - Use a parent `JMenu` for Tacklezones.
  - Create submenus for each option (e.g., Home, Away, Opposing, No Overlap, Contour).
  - Each submenu has two `JRadioButtonMenuItem`s: ON and OFF.
  - Example variable declaration (class fields):

    ```java
    private JRadioButtonMenuItem fTzHomeOnMenuItem;
    private JRadioButtonMenuItem fTzHomeOffMenuItem;
    // ...etc for each option
    ```

  - Example instantiation (in `createTacklezonesMenu`):

    ```java
    fTzHomeOnMenuItem = new JRadioButtonMenuItem(...);
    fTzHomeOffMenuItem = new JRadioButtonMenuItem(...);
    // ...add to ButtonGroup, add listeners, add to menu
    ```

---

### 4. **Initialize Menu State** (`refresh` Method, usually in `GameMenuBar.java`)

- **Purpose:** Ensure the UI reflects the current setting value at startup and after changes.
- **Typical pattern:**

  ```java
  String tzHomeSetting = getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_HOME);
  fTzHomeOnMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_HOME_ON.equals(tzHomeSetting));
  fTzHomeOffMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_HOME_OFF.equals(tzHomeSetting));
  ```

  - Repeat for each subsetting.

---

### 5. **Handle Menu Actions** (`actionPerformed` Method, usually in `GameMenuBar.java`)

- **Purpose:** When a menu item is clicked, save the new setting.
- **Example:**

  ```java
  if (source == fTzHomeOnMenuItem) {
      getClient().setProperty(CommonProperty.SETTING_TACKLEZONES_HOME, IClientPropertyValue.SETTING_TACKLEZONES_HOME_ON);
  } else if (source == fTzHomeOffMenuItem) {
      getClient().setProperty(CommonProperty.SETTING_TACKLEZONES_HOME, IClientPropertyValue.SETTING_TACKLEZONES_HOME_OFF);
  }
  // ...repeat for each option
  ```

---

### 6. **Consume the Setting in Feature Code**

- **Purpose:** Use the setting to control feature logic (e.g., tacklezone rendering).
- **Pattern:**

  - Implement convenience methods where needed:

    ```java
    private boolean isHomeTzEnabled() {
      return IClientPropertyValue.SETTING_TACKLEZONES_HOME_ON.equals(
        getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_HOME));
    }
    // ...repeat for other toggles
    ```

  - Reference these in the relevant update/draw methods to enable or disable behavior.

---

## **Summary Table**

| Step | File                        | What to Add                             |
| ---- | --------------------------- | --------------------------------------- |
| 1    | `CommonProperty.java`       | Keys for each new setting               |
| 2    | `IClientPropertyValue.java` | ON/OFF values for each setting          |
| 3    | `GameMenuBar.java`          | Menu code to allow user to toggle       |
| 4    | `GameMenuBar.java`          | Sync menu selection with property state |
| 5    | `GameMenuBar.java`          | Save setting change on menu action      |
| 6    | Feature/Layer Class         | Use property to control logic/behavior  |

---

**Tip:**
This pattern is consistent for adding almost any new user-configurable option in the FFB Java client. Copy/adapt as needed for future settings.

---
