package com.balancedbytes.games.ffb;


/**
 * 
 * @author Kalimar
 */
public enum DodgeModifier implements IRollModifier {
  
  // TODO: create factory for this
  
  STUNTY(1, "Stunty", 0, false, false),
  BREAK_TACKLE(2, "Break Tackle", 0, false, false),
  TWO_HEADS(3, "Two Heads", -1, false, false),
  DIVING_TACKLE(4, "Diving Tackle", 2, false, false),
  TITCHY(5, "Titchy", -1, false, false),
  TACKLEZONES_1(6, "1 Tacklezone", 1, true, false),
  TACKLEZONES_2(7, "2 Tacklezones", 2, true, false),
  TACKLEZONES_3(8, "3 Tacklezones", 3, true, false),
  TACKLEZONES_4(9, "4 Tacklezones", 4, true, false),
  TACKLEZONES_5(10, "5 Tacklezones", 5, true, false),
  TACKLEZONES_6(11, "6 Tacklezones", 6, true, false),
  TACKLEZONES_7(12, "7 Tacklezones", 7, true, false),
  TACKLEZONES_8(13, "8 Tacklezones", 8, true, false),
  PREHENSILE_TAIL_1(14, "1 Prehensile Tail", 1, false, true),
  PREHENSILE_TAIL_2(15, "2 Prehensile Tails", 2, false, true),
  PREHENSILE_TAIL_3(16, "3 Prehensile Tails", 3, false, true),
  PREHENSILE_TAIL_4(17, "4 Prehensile Tails", 4, false, true),
  PREHENSILE_TAIL_5(18, "5 Prehensile Tails", 5, false, true),
  PREHENSILE_TAIL_6(19, "6 Prehensile Tails", 6, false, true),
  PREHENSILE_TAIL_7(20, "7 Prehensile Tails", 7, false, true),
  PREHENSILE_TAIL_8(21, "8 Prehensile Tails", 8, false, true);
  
  private int fId;
  private String fName;
  private int fModifier;
  private boolean fTacklezoneModifier;
  private boolean fPrehensileTailModifier;
  
  private DodgeModifier(int pId, String pName, int pModifier, boolean pTacklezoneModifier, boolean pPrehensileTailModifier) {
    fId = pId;
    fName = pName;
    fModifier = pModifier;
    fTacklezoneModifier = pTacklezoneModifier;
    fPrehensileTailModifier = pPrehensileTailModifier;
  }
  
  public int getId() {
    return fId;
  }
  
  public int getModifier() {
    return fModifier;
  }
  
  public String getName() {
    return fName;
  }

  public boolean isTacklezoneModifier() {
    return fTacklezoneModifier;
  }
  
  public boolean isPrehensileTailModifier() {
    return fPrehensileTailModifier;
  }
    
  public boolean isModifierIncluded() {
    return (isTacklezoneModifier() || isPrehensileTailModifier());
  }

}
