package com.balancedbytes.games.ffb.model;

import java.util.Comparator;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.SkillCategory;

public class Skill implements INamedObject {

  private String name;
  private SkillCategory category;
  
  public Skill(String name, SkillCategory category) {
    this.name = name;
    this.category = category;
  }
  
  @Override
  public String getName() {
    return name;
  }
  
  public SkillCategory getCategory() {
    return category;
  }

  public boolean equals(Object other) {
    return name != null && other instanceof Skill && name.equals(((Skill)other).name);
  }
  
  public static Comparator<Skill> getComparator() {
    return new Comparator<Skill>() {
      public int compare(Skill a, Skill b) {
        return a.getName().compareTo(b.getName());
      }
    };
  }
  
  public int getCost(Player player) {
    Position position = player.getPosition();
    if (position.hasSkill(this)) {
      return 0;
    }
    if (position.isDoubleCategory(category)) {
      return 30000;
    } else {
      return 20000;
    }
  }

  public String[] getSkillUseDescription() {
    return null;
  }

}
