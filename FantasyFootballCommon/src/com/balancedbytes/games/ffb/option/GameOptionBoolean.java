package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class GameOptionBoolean implements IGameOption {
  
  private GameOptionId fId;
  private boolean fDefault;
  private boolean fValue;
  private String fMessageTrue;
  private String fMessageFalse;

  public GameOptionBoolean(GameOptionId pId) {
    fId = pId;
  }

  @Override
  public GameOptionId getId() {
    return fId;
  }

  private boolean getDefault() {
    return fDefault;
  }

  public GameOptionBoolean setDefault(boolean pDefault) {
    fDefault = pDefault;
    return this;
  }
  
  @Override
  public String getValueAsString() {
    return Boolean.toString(getValue());
  }
  
  public boolean getValue() {
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

  @Override
  public boolean isChanged() {
    return (getDefault() != getValue()); 
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
    return (getValue() ? fMessageTrue : fMessageFalse);
  }

  @Override
  public GameOptionBoolean reset() {
    setValue(getDefault());
    return this;
  }

}
