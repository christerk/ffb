package com.balancedbytes.games.ffb;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.util.StringTool;

/**
 * LRB5 Skill Categories
 * 
 * @author Kalimar
 */
public enum SkillCategory {
  
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
  
  public static SkillCategory fromId(int pId) {
    if (pId > 0) {
      for (SkillCategory skillCategory : values()) {
        if (pId == skillCategory.getId()) {
          return skillCategory;
        }
      }
    }
    return null;
  }
  
  public static SkillCategory fromName(String pName) {
    if (StringTool.isProvided(pName)) {
      for (SkillCategory skillCategory : values()) {
        if (pName.equalsIgnoreCase(skillCategory.getName())) {
          return skillCategory;
        }
      }
    }
    return null;
  }
  
  public static SkillCategory fromTypeString(String pTypeString) {
    if (StringTool.isProvided(pTypeString)) {
      for (SkillCategory skillCategory : values()) {
        if (pTypeString.equalsIgnoreCase(skillCategory.getTypeString())) {
          return skillCategory;
        }
      }
    }
    return null;
  }
  
  public static SkillCategory[] fromTypeStrings(String pTypeStrings) {
    List<SkillCategory> skillCategories = new ArrayList<SkillCategory>();
    if (StringTool.isProvided(pTypeStrings)) {
      for (int i = 0; i < pTypeStrings.length(); i++) {
        SkillCategory skillCategory = fromTypeString(pTypeStrings.substring(i, i + 1));
        if (skillCategory != null) {
          skillCategories.add(skillCategory);
        }
      }
    }
    return skillCategories.toArray(new SkillCategory[skillCategories.size()]);
  }

}
