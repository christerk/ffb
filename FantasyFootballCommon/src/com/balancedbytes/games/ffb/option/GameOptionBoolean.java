package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class GameOptionBoolean extends GameOptionAbstract {
  
  private boolean fDefault;
  private boolean fValue;
  private String fMessageTrue;
  private String fMessageFalse;

  public GameOptionBoolean(GameOptionId pId) {
    super(pId);
  }

  protected boolean getDefault() {
    return fDefault;
  }
  
  @Override
  protected String getDefaultAsString() {
    return Boolean.toString(getDefault());
  }

  public GameOptionBoolean setDefault(boolean pDefault) {
    fDefault = pDefault;
    return setValue(getDefault());
  }
  
  @Override
  public String getValueAsString() {
    return Boolean.toString(isEnabled());
  }
  
  public boolean isEnabled() {
    return fValue;
  }
  
  @Override
  public GameOptionBoolean setValue(String pValue) {
    if (!StringTool.isProvided(pValue) || "0".equals(pValue)) {
      return setValue(false);
    } else if ("1".equals(pValue)) {
      return setValue(true);
    } else {
      return setValue(Boolean.getBoolean(pValue));
    }
  }

  public GameOptionBoolean setValue(boolean pValue) {
    fValue = pValue;
    return this;
  }

  public GameOptionBoolean setMessageTrue(String pMessage) {
    fMessageTrue = pMessage;
    return this;
  }

  public GameOptionBoolean setMessageFalse(String pMessage) {
    fMessageFalse = pMessage;
    return this;
  }

  @Override
  public String getDisplayMessage() {
    return (isEnabled() ? fMessageTrue : fMessageFalse);
  }

}
