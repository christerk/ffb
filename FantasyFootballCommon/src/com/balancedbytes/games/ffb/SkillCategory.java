package com.balancedbytes.games.ffb;


/**
 * LRB5 Skill Categories
 * 
 * @author Kalimar
 */
public enum SkillCategory implements IEnumWithId, IEnumWithName {
  
  GENERAL(1, "General", "G"),
  AGILITY(2, "Agility", "A"),
  PASSING(3, "Passing", "P"),
  STRENGTH(4, "Strength", "S"),
  MUTATION(5, "Mutation", "M"),
  EXTRAORDINARY(6, "Extraordinary", "E"),
  STAT_INCREASE(7, "Stat Increase", "+");
  
  private int fId;
  private String fName;
  private String fTypeString;
  
  private SkillCategory(int pId, String pName, String pTypeString) {
    fId = pId;
    fName = pName;
    fTypeString = pTypeString;
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
  
}
