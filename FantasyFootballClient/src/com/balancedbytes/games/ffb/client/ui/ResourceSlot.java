package com.balancedbytes.games.ffb.client.ui;

import java.awt.Rectangle;

/**
 * 
 * @author Kalimar
 */
public class ResourceSlot {
  
  public static final int TYPE_NONE = 0;
  public static final int TYPE_RE_ROLL = 1;
  public static final int TYPE_APOTHECARY = 2;
  public static final int TYPE_BRIBE = 3;
  public static final int TYPE_BLOODWEISER_BABE = 4;
  public static final int TYPE_MASTER_CHEF = 5;
  public static final int TYPE_IGOR = 6;
  public static final int TYPE_WIZARD = 7;
  public static final int TYPE_CARD = 8;
  
  private int fType;
  private Rectangle fLocation;
  private int fValue;
  private boolean fEnabled;
  private String fIconProperty;
  
  public ResourceSlot(Rectangle pLocation) {
    fLocation = pLocation;
    fEnabled = true;
  }
  
  public int getType() {
    return fType;
  }
  
  public void setType(int pType) {
    fType = pType;
  }
  
  public Rectangle getLocation() {
    return fLocation;
  }
  
  public int getValue() {
    return fValue;
  }
  
  public void setValue(int pValue) {
    fValue = pValue;
  }
  
  public void setIconProperty(String pIconProperty) {
  	fIconProperty = pIconProperty;
  }
  
  public String getIconProperty() {
  	return fIconProperty;
  }
  
  public String getToolTip() {
    if (getType() > 0) {
      StringBuilder toolTip = new StringBuilder();
      if (getValue() > 0) {
        toolTip.append(getValue()).append(" ");
      } else {
        toolTip.append("No ");
      }
      switch (getType()) {
        case TYPE_RE_ROLL:
          toolTip.append((getValue() == 1) ? "Re-Roll" : "Re-Rolls");
          break;
        case TYPE_APOTHECARY:
          toolTip.append((getValue() == 1) ? "Apothecary" : "Apothecaries");
          break;
        case TYPE_BRIBE:
          toolTip.append((getValue() == 1) ? "Bribe" : "Bribes");
          break;
        case TYPE_BLOODWEISER_BABE:
          toolTip.append((getValue() == 1) ? "Bloodweiser Keg" : "Bloodweiser Kegs");
          break;
        case TYPE_MASTER_CHEF:
          toolTip.append((getValue() == 1) ? "Master Chef" : "Master Chefs");
          break;
        case TYPE_IGOR:
          toolTip.append((getValue() == 1) ? "Igor" : "Igors");
          break;
        case TYPE_WIZARD:
          toolTip.append((getValue() == 1) ? "Wizard" : "Wizards");
          break;
        case TYPE_CARD:
          toolTip.append((getValue() == 1) ? "Card" : "Cards");
          break;
      }
      return toolTip.toString();
    } else {
      return null;
    }
  }
  
  public void setEnabled(boolean pEnabled) {
    fEnabled = pEnabled;
  }
  
  public boolean isEnabled() {
    return fEnabled;    
  }
  
}
