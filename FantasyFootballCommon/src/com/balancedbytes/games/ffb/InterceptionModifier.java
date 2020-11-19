package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.InterceptionModifiers.InterceptionContext;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class InterceptionModifier implements IRollModifier {
  private String fName;
  private int fModifier;
  private boolean fTacklezoneModifier;
  private boolean fDisturbingPresenceModifier;
  
  InterceptionModifier(String pName, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
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
  
  public boolean appliesToContext(Skill skill, InterceptionContext context) {
	  return true;
  }
  
}
