package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class GameOptionInt implements IGameOption {
  
  private GameOptionId fId;
  private int fDefault;
  private int fValue;
  private String fMessage;

  public GameOptionInt(GameOptionId pId) {
    fId = pId;
  }

  @Override
  public GameOptionId getId() {
    return fId;
  }
  
  private int getDefault() {
    return fDefault;
  }

  public GameOptionInt setDefault(int pDefault) {
    fDefault = pDefault;
    return this;
  }
  
  @Override
  public String getValueAsString() {
    return Integer.toString(getValue());
  }
  
  public int getValue() {
    return fValue;
  }
  
  @Override
  public GameOptionInt setValue(String pValue) {
    if (StringTool.isProvided(pValue)) {
      return setValue(Integer.parseInt(pValue));
    } else {
      return setValue(0);
    }
  }
  
  public GameOptionInt setValue(int pValue) {
    fValue = pValue;
    return this;
  }

  @Override
  public boolean isChanged() {
    return (getDefault() != getValue());
  }

  public GameOptionInt setMessage(String pMessage) {
    fMessage = pMessage;
    return this;
  }

  @Override
  public String getDisplayMessage() {
    return StringTool.bind(fMessage, StringTool.formatThousands(getValue()));
  }

  @Override
  public GameOptionInt reset() {
    setValue(getDefault());
    return this;
  }

}
