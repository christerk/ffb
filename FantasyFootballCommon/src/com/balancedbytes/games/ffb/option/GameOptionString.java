package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class GameOptionString implements IGameOption {
  
  private GameOptionId fId;
  private String fDefault;
  private String fValue;
  private String fMessage;

  public GameOptionString(GameOptionId pId) {
    fId = pId;
  }

  @Override
  public GameOptionId getId() {
    return fId;
  }
  
  private String getDefault() {
    return fDefault;
  }

  public GameOptionString setDefault(String pDefault) {
    fDefault = pDefault;
    return this;
  }
  
  @Override
  public String getValueAsString() {
    return getValue();
  }
  
  public String getValue() {
    return fValue;
  }
  
  @Override
  public GameOptionString setValue(String pValue) {
    fValue = pValue;
    return this;
  }

  @Override
  public boolean isChanged() {
    return !StringTool.print(getDefault()).equals(getValueAsString());
  }

  public GameOptionString setMessage(String pMessage) {
    fMessage = pMessage;
    return this;
  }

  @Override
  public String getDisplayMessage() {
    return StringTool.bind(fMessage, getValueAsString());
  }

  @Override
  public GameOptionString reset() {
    setValue(getDefault());
    return this;
  }

}
