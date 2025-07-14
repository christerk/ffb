## FFB Client: Adding Multi-Mode User Settings

This guide documents the conventions for adding user settings to the FFB Java client using the revised Tacklezones feature as an example.

---

#### 1: Register Properties in `CommonProperty.java`

- **A. Add a new enum entry** for each setting at the top of the enum, e.g.:

  ```java
  // Tacklezones
  SETTING_TACKLEZONES_PLAYER_MODE("setting.tacklezones.playerMode", "Player Mode", "Gameplay", "Tacklezones: Player Mode"),
  SETTING_TACKLEZONES_SPECTATOR_MODE("setting.tacklezones.spectatorMode", "Spectator Mode", "Gameplay", "Tacklezones: Spectator Mode"),
  SETTING_TACKLEZONES_NO_OVERLAP("setting.tacklezones.noOverlap", "NO Overlap", "Gameplay"),
  SETTING_TACKLEZONES_CONTOUR("setting.tacklezones.contour", "Contour", "Gameplay"),
  ```

- **B. Add the new properties to the `_SAVED_USER_SETTINGS` array** so they persist between sessions and show up in config dialogs:

  ```java
  public static final CommonProperty[] _SAVED_USER_SETTINGS = {
      ...,
      SETTING_TACKLEZONES_PLAYER_MODE, SETTING_TACKLEZONES_SPECTATOR_MODE,
      SETTING_TACKLEZONES_NO_OVERLAP, SETTING_TACKLEZONES_CONTOUR,
  };
  ```

---

### 2. **Define Setting Values** (`IClientPropertyValue.java`)

- **Purpose:** Enumerate all possible states.
- **Location:** `IClientPropertyValue.java`
- **Example:**

  ```java
  // Single-selection values
  String SETTING_TACKLEZONES_NONE    = "none";
  String SETTING_TACKLEZONES_HOME    = "home";
  String SETTING_TACKLEZONES_AWAY    = "away";
  String SETTING_TACKLEZONES_BOTH    = "both";
  String SETTING_TACKLEZONES_PASSIVE = "passive";
  // Global toggles
  String SETTING_TACKLEZONES_NO_OVERLAP_ON  = "noOverlapTzOn";
  String SETTING_TACKLEZONES_NO_OVERLAP_OFF = "noOverlapTzOff";
  String SETTING_TACKLEZONES_CONTOUR_ON     = "tzContourOn";
  String SETTING_TACKLEZONES_CONTOUR_OFF    = "tzContourOff";
  ```

---

### 3. **Create Menu UI** (`GameMenuBar.java`)

- **Purpose:** Present one set of choices per mode, and global toggles.
- **Pattern:**

  - Main menu: **Tacklezones**
  - Submenus: **Player Mode**, **Spectator Mode** (each with a radio group: None, Home, Away, Both, Passive)
  - Below: **No Overlap** and **Contour** (ON/OFF radio groups, global for both modes)

- **Variable declarations (fields):**

  ```java
  // Player mode
  private JRadioButtonMenuItem fTzPlayerNoneMenuItem;
  private JRadioButtonMenuItem fTzPlayerHomeMenuItem;
  private JRadioButtonMenuItem fTzPlayerAwayMenuItem;
  private JRadioButtonMenuItem fTzPlayerBothMenuItem;
  private JRadioButtonMenuItem fTzPlayerPassiveMenuItem;
  // Spectator mode
  private JRadioButtonMenuItem fTzSpecNoneMenuItem;
  private JRadioButtonMenuItem fTzSpecHomeMenuItem;
  private JRadioButtonMenuItem fTzSpecAwayMenuItem;
  private JRadioButtonMenuItem fTzSpecBothMenuItem;
  private JRadioButtonMenuItem fTzSpecPassiveMenuItem;
  // Global
  private JRadioButtonMenuItem fTzNoOverlapOnMenuItem;
  private JRadioButtonMenuItem fTzNoOverlapOffMenuItem;
  private JRadioButtonMenuItem fTzContourOnMenuItem;
  private JRadioButtonMenuItem fTzContourOffMenuItem;
  ```

---

### 4. **Initialize Menu State** (`refresh` Method)

- **Purpose:** Reflect the current value in the UI for each group.
- **Pattern:**

  ```java
  String playerMode = getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE);
  fTzPlayerNoneMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_NONE.equals(playerMode));
  fTzPlayerHomeMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_HOME.equals(playerMode));
  // ...etc for Away/Both/Passive

  String specMode = getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_SPECTATOR_MODE);
  // ...repeat for each

  String noOverlap = getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_NO_OVERLAP);
  fTzNoOverlapOnMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_NO_OVERLAP_ON.equals(noOverlap));
  fTzNoOverlapOffMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_NO_OVERLAP_OFF.equals(noOverlap));

  String contour = getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_CONTOUR);
  fTzContourOnMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_CONTOUR_ON.equals(contour));
  fTzContourOffMenuItem.setSelected(IClientPropertyValue.SETTING_TACKLEZONES_CONTOUR_OFF.equals(contour));
  ```

---

### 5. **Handle Menu Actions** (`actionPerformed` Method)

- **Purpose:** Save the selected value to the relevant property.
- **Pattern:**

  ```java
  if (source == fTzPlayerNoneMenuItem) {
    getClient().setProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE, IClientPropertyValue.SETTING_TACKLEZONES_NONE);
  }
  if (source == fTzPlayerHomeMenuItem) {
    getClient().setProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE, IClientPropertyValue.SETTING_TACKLEZONES_HOME);
  }
  // ...etc for other player and spec items

  if (source == fTzNoOverlapOnMenuItem) {
    getClient().setProperty(CommonProperty.SETTING_TACKLEZONES_NO_OVERLAP, IClientPropertyValue.SETTING_TACKLEZONES_NO_OVERLAP_ON);
  }
  // ...etc for all items, with saveUserSettings(true) as appropriate
  ```

---

### 6. **Consume the Setting in Feature Code**

- **Purpose:** Use the selected value (per mode) to drive behavior.
- **Pattern:**

  ```java
  // To get the correct setting for the current client mode:
  String tzSetting;
  if (getClient().getMode() == ClientMode.SPECTATOR) {
      tzSetting = getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_SPECTATOR_MODE);
  } else {
      tzSetting = getClient().getProperty(CommonProperty.SETTING_TACKLEZONES_PLAYER_MODE);
  }
  // Then compare tzSetting to IClientPropertyValue constants as needed.
  ```

---

## **Summary Table**

| Step | File                        | What to Add                                 |
| ---- | --------------------------- | ------------------------------------------- |
| 1    | `CommonProperty.java`       | Keys for each new setting                   |
| 2    | `IClientPropertyValue.java` | String values for single-select/radio style |
| 3    | `GameMenuBar.java`          | Menu for Player, Spectator, and global      |
| 4    | `GameMenuBar.java`          | Sync menu items with property state         |
| 5    | `GameMenuBar.java`          | Save setting change on action               |
| 6    | Feature/Layer Class         | Use property in logic/behavior              |

---

**Tip:**
This pattern is now the reference for any _radio group_ user setting with multiple modes and shared/global toggles.
