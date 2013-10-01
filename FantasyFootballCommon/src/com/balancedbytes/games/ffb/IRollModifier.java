package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public interface IRollModifier {
  
  public String getName();
  
  public int getId();
  
  public int getModifier();
  
  public boolean isModifierIncluded();

}
