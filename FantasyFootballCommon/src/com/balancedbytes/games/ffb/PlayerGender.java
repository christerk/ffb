package com.balancedbytes.games.ffb;

public enum PlayerGender {
  
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
  
  public static PlayerGender fromId(int pId) {
    for (PlayerGender gender : values()) {
      if (gender.getId() == pId) {
        return gender;
      }
    }
    return null;
  }

  public static PlayerGender fromName(String pName) {
    if (pName != null) {
      for (PlayerGender gender : values()) {
        if (pName.equalsIgnoreCase(gender.getName())) {
          return gender;
        }
      }
    }
    return null;
  }

  public static PlayerGender fromTypeString(String pTypeString) {
    if (pTypeString != null) {
      for (PlayerGender gender : values()) {
        if (pTypeString.equalsIgnoreCase(gender.getTypeString())) {
          return gender;
        }
      }
    }
    return null;
  }

}
