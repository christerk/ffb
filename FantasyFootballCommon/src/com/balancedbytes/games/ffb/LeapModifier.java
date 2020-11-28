package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.LeapModifiers.LeapContext;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class LeapModifier implements IRollModifier {
  
  // TODO: create factory for this
  
  private String fName;
  private int fModifier;
  
  LeapModifier(String pName, int pModifier) {
    fName = pName;
    fModifier = pModifier;
  }
  
  public int getModifier() {
    return fModifier;
  }
  
  public String getName() {
    return fName;
  }
  
  public boolean isModifierIncluded() {
    return false;
  }
  
  public boolean appliesToContext(Skill skill, LeapContext context) {
    return true;
  }
}
