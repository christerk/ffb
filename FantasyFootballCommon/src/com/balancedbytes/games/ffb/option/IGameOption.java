package com.balancedbytes.games.ffb.option;


/**
 * 
 * @author Kalimar
 */
public interface IGameOption {

  public GameOptionId getId();
  
  public String getValueAsString();
  
  public IGameOption setValue(String pValue);
  
  public boolean isChanged();
  
  public String getDisplayMessage();
  
  public IGameOption reset();
  
}
