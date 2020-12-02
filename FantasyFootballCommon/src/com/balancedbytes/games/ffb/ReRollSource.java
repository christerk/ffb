package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class ReRollSource implements INamedObject {
 

  private int id;
  private String name;
  private Class<? extends Skill> skill;
  
  ReRollSource(String pName) {
    name = pName;
  }
  
  ReRollSource(Class<? extends Skill> skill) {
    this(skill.getName());
    this.skill = skill;
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public Skill getSkill() {
	  SkillFactory skillFactory = new SkillFactory();
	  if(skill != null) {
		  return skillFactory.forClass(skill);
	  }
	  else {
		  return skillFactory.forName(name);
	  }
  }
  
}
