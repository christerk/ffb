package com.balancedbytes.games.ffb;


/**
 * LRB5 Skill Categories
 * 
 * @author Kalimar
 */
public enum SkillCategory implements IEnumWithName {
  
  GENERAL("General", "G"),
  AGILITY("Agility", "A"),
  PASSING("Passing", "P"),
  STRENGTH("Strength", "S"),
  MUTATION("Mutation", "M"),
  EXTRAORDINARY("Extraordinary", "E"),
  STAT_INCREASE("Stat Increase", "+"),
  STAT_DECREASE("Stat Decrease", "-");
  
  private String fName;
  private String fTypeString;
  
  private SkillCategory(String pName, String pTypeString) {
    fName = pName;
    fTypeString = pTypeString;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getTypeString() {
    return fTypeString;
  }
  
}
