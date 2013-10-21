package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public enum PlayerGender implements IEnumWithId, IEnumWithName {
  
  MALE(1, "male", "M", "he", "his", "him", "himself"),
  FEMALE(2, "female", "F", "she", "her", "her", "herself"),
  NEUTRAL(3, "neutral", "N", "it", "its", "it", "itself");
  
  private int fId;
  private String fName;
  private String fTypeString;
  private String fNominative;
  private String fGenitive;
  private String fDative;
  private String fSelf;

  private PlayerGender(int pId, String pName, String pTypeString, String pNominative, String pGenitive, String pDative, String pSelf) {
    fId = pId;
    fName = pName;
    fTypeString = pTypeString;
    fNominative = pNominative;
    fGenitive = pGenitive;
    fDative = pDative;
    fSelf = pSelf;
  }
  
  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getTypeString() {
    return fTypeString;
  }
  
  public String getNominative() {
    return fNominative;
  }
  
  public String getGenitive() {
    return fGenitive;
  }
  
  public String getDative() {
    return fDative;
  }
  
  public String getSelf() {
    return fSelf;
  }

}
