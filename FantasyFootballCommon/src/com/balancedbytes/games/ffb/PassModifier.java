package com.balancedbytes.games.ffb;



/**
 * 
 * @author Kalimar
 */
public enum PassModifier implements IRollModifier {
  
  ACCURATE("Accurate", -1, false, false),
  NERVES_OF_STEEL("Nerves of Steel", 0, false, false),
  STRONG_ARM("Strong Arm", -1, false, false),
  VERY_SUNNY("Very Sunny", 1, false, false),
  BLIZZARD("Blizzard", 0, false, false),
  STUNTY("Stunty", 1, false, false),
  TACKLEZONES_1("1 Tacklezone", 1, true, false),
  TACKLEZONES_2("2 Tacklezones", 2, true, false),
  TACKLEZONES_3("3 Tacklezones", 3, true, false),
  TACKLEZONES_4("4 Tacklezones", 4, true, false),
  TACKLEZONES_5("5 Tacklezones", 5, true, false),
  TACKLEZONES_6("6 Tacklezones", 6, true, false),
  TACKLEZONES_7("7 Tacklezones", 7, true, false),
  TACKLEZONES_8("8 Tacklezones", 8, true, false),
  DISTURBING_PRESENCES_1("1 Disturbing Presence", 1, false, true),
  DISTURBING_PRESENCES_2("2 Disturbing Presences", 2, false, true),
  DISTURBING_PRESENCES_3("3 Disturbing Presences", 3, false, true),
  DISTURBING_PRESENCES_4("4 Disturbing Presences", 4, false, true),
  DISTURBING_PRESENCES_5("5 Disturbing Presences", 5, false, true),
  DISTURBING_PRESENCES_6("6 Disturbing Presences", 6, false, true),
  DISTURBING_PRESENCES_7("7 Disturbing Presences", 7, false, true),
  DISTURBING_PRESENCES_8("8 Disturbing Presences", 8, false, true),
  DISTURBING_PRESENCES_9("9 Disturbing Presences", 9, false, true),
  DISTURBING_PRESENCES_10("10 Disturbing Presences", 10, false, true),
  DISTURBING_PRESENCES_11("11 Disturbing Presences", 11, false, true),
  THROW_TEAM_MATE("Throw Team-Mate", 1, false, false),
  GROMSKULLS_EXPLODING_RUNES("Gromskull's Exploding Runes", 1, false, false);

  private String fName;
  private int fModifier;
  private boolean fTacklezoneModifier;
  private boolean fDisturbingPresenceModifier;
  
  private PassModifier(String pName, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
    fName = pName;
    fModifier = pModifier;
    fTacklezoneModifier = pTacklezoneModifier;
    fDisturbingPresenceModifier = pDisturbingPresenceModifier;
  }
  
  public String getName() {
    return fName;
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
