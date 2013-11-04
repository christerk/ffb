package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum ArmorModifier implements IEnumWithId, IEnumWithName {
  
  CLAWS(1, "Claws", 0, false),
  MIGHTY_BLOW(2, "Mighty Blow", 1, false),
  FOUL_PLUS_1(3, "1 Offensive Assist", 1, true),
  FOUL_PLUS_2(4, "2 Offensive Assists", 2, true),
  FOUL_PLUS_3(5, "3 Offensive Assists", 3, true),
  FOUL_PLUS_4(6, "4 Offensive Assists", 4, true),
  FOUL_PLUS_5(7, "5 Offensive Assists", 5, true),
  FOUL_PLUS_6(8, "6 Offensive Assists", 6, true),
  FOUL_PLUS_7(9, "7 Offensive Assists", 7, true),
  FOUL_MINUS_1(10, "1 Defensive Assist", -1, true),
  FOUL_MINUS_2(11, "2 Defensive Assists", -2, true),
  FOUL_MINUS_3(12, "3 Defensive Assists", -3, true),
  FOUL_MINUS_4(13, "4 Defensive Assists", -4, true),
  FOUL_MINUS_5(14, "5 Defensive Assists", -5, true),
  DIRTY_PLAYER(15, "Dirty Player", 1, false),
  STAKES(16, "Stakes", 1, false),
  CHAINSAW(17, "Chainsaw", 3, false),
  FOUL(18, "Foul", 1, false);
  
  private int fId;
  private String fName;
  private int fModifier;
  private boolean fFoulAssistModifier; 
  
  private ArmorModifier(int pId, String pName, int pModifier, boolean pFoulAssistModifier) {
    fId = pId;
    fName = pName;
    fModifier = pModifier;
    fFoulAssistModifier = pFoulAssistModifier;
  }
  
  public int getModifier() {
    return fModifier;
  }
  
  public String getName() {
    return fName;
  }
  
  public int getId() {
    return fId;
  }
  
  public boolean isFoulAssistModifier() {
    return fFoulAssistModifier;
  }
  
}
