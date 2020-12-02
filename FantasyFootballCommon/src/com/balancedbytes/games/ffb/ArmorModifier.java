package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.ArmorModifiers.ArmorModifierContext;

/**
 * 
 * @author Kalimar
 */
public class ArmorModifier implements INamedObject {

  
  private String fName;
  private int fModifier;
  private boolean fFoulAssistModifier; 
  
  ArmorModifier(String pName, int pModifier, boolean pFoulAssistModifier) {
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
  
  public boolean isFoulAssistModifier() {
    return fFoulAssistModifier;
  }

  public boolean appliesToContext(ArmorModifierContext context) {
	  return true;
  }
  
}
