package com.balancedbytes.games.ffb;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.util.StringTool;


/**
 * 
 * @author Kalimar
 */
public class SkillCategoryFactory implements IEnumWithIdFactory, IEnumWithNameFactory {

  public SkillCategory forId(int pId) {
    if (pId > 0) {
      for (SkillCategory skillCategory : SkillCategory.values()) {
        if (pId == skillCategory.getId()) {
          return skillCategory;
        }
      }
    }
    return null;
  }
  
  public SkillCategory forName(String pName) {
    if (StringTool.isProvided(pName)) {
      for (SkillCategory skillCategory : SkillCategory.values()) {
        if (pName.equalsIgnoreCase(skillCategory.getName())) {
          return skillCategory;
        }
      }
    }
    return null;
  }
  
  public SkillCategory forTypeString(String pTypeString) {
    if (StringTool.isProvided(pTypeString)) {
      for (SkillCategory skillCategory : SkillCategory.values()) {
        if (pTypeString.equalsIgnoreCase(skillCategory.getTypeString())) {
          return skillCategory;
        }
      }
    }
    return null;
  }
  
  public SkillCategory[] forTypeStrings(String pTypeStrings) {
    List<SkillCategory> skillCategories = new ArrayList<SkillCategory>();
    if (StringTool.isProvided(pTypeStrings)) {
      for (int i = 0; i < pTypeStrings.length(); i++) {
        SkillCategory skillCategory = forTypeString(pTypeStrings.substring(i, i + 1));
        if (skillCategory != null) {
          skillCategories.add(skillCategory);
        }
      }
    }
    return skillCategories.toArray(new SkillCategory[skillCategories.size()]);
  }

}
