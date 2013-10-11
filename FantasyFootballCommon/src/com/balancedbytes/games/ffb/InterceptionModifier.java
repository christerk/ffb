package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum InterceptionModifier implements IRollModifier {
  
  NERVES_OF_STEEL(1, "Nerves of Steel", 0, false, false),
  EXTRA_ARMS(2, "Extra Arms", -1, false, false),
  VERY_LONG_LEGS(3, "Very Long Legs", -1, false, false),
  POURING_RAIN(4, "Pouring Rain", 1, false, false),
  TACKLEZONES_1(5, "1 Tacklezone", 1, true, false),
  TACKLEZONES_2(6, "2 Tacklezones", 2, true, false),
  TACKLEZONES_3(7, "3 Tacklezones", 3, true, false),
  TACKLEZONES_4(8, "4 Tacklezones", 4, true, false),
  TACKLEZONES_5(9, "5 Tacklezones", 5, true, false),
  TACKLEZONES_6(10, "6 Tacklezones", 6, true, false),
  TACKLEZONES_7(11, "7 Tacklezones", 7, true, false),
  TACKLEZONES_8(12, "8 Tacklezones", 8, true, false),
  DISTURBING_PRESENCES_1(13, "1 Disturbing Presence", 1, false, true),
  DISTURBING_PRESENCES_2(14, "2 Disturbing Presences", 2, false, true),
  DISTURBING_PRESENCES_3(15, "3 Disturbing Presences", 3, false, true),
  DISTURBING_PRESENCES_4(16, "4 Disturbing Presences", 4, false, true),
  DISTURBING_PRESENCES_5(17, "5 Disturbing Presences", 5, false, true),
  DISTURBING_PRESENCES_6(18, "6 Disturbing Presences", 6, false, true),
  DISTURBING_PRESENCES_7(19, "7 Disturbing Presences", 7, false, true),
  DISTURBING_PRESENCES_8(20, "8 Disturbing Presences", 8, false, true),
  DISTURBING_PRESENCES_9(21, "9 Disturbing Presences", 9, false, true),
  DISTURBING_PRESENCES_10(22, "10 Disturbing Presences", 10, false, true),
  DISTURBING_PRESENCES_11(23, "11 Disturbing Presences", 11, false, true),
  FAWNDOUGHS_HEADBAND(24, "Fawndough's Headband", -1, false, false),
  MAGIC_GLOVES_OF_JARK_LONGARM(25, "Magic Gloves of Jark Longarm", -1, false, false);

  private int fId;
  private String fName;
  private int fModifier;
  private boolean fTacklezoneModifier;
  private boolean fDisturbingPresenceModifier;
  
  private InterceptionModifier(int pId, String pName, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
    fId = pId;
    fName = pName;
    fModifier = pModifier;
    fTacklezoneModifier = pTacklezoneModifier;
    fDisturbingPresenceModifier = pDisturbingPresenceModifier;
  }
  
  public String getName() {
    return fName;
  }
  
  public int getId() {
    return fId;
  }
  
  public int getModifier() {
    return fModifier;
  }
  
  public boolean isTacklezoneModifier() {
    return fTacklezoneModifier;
  }
  
  public boolean isDisturbingPresenceModifier() {
    return fDisturbingPresenceModifier;
  }
  
  public boolean isModifierIncluded() {
    return (isTacklezoneModifier() || isDisturbingPresenceModifier());
  }
  
}
