package com.balancedbytes.games.ffb;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Hashtable;

import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class SkillFactory implements INamedObjectFactory {
  private Hashtable<String, Skill> skills;
  
  public SkillFactory(boolean isServer) {
    skills = new Hashtable<String, Skill>();
    
    try {
      Class constants = Class.forName(isServer ? "ServerSkillConstants" : "ClientSkillConstants");
      Field[] fields = constants.getFields();
      for (Field field : fields) {
        int modifiers = field.getModifiers();
        if (Modifier.isStatic(modifiers) && Skill.class.isAssignableFrom(field.getType())) {
          addSkill((Skill) field.get(null));
        }
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public Collection<Skill> getSkills() {
    return skills.values();
  }
  
  private void addSkill(Skill skill) {
    skills.put(skill.getName(), skill);
  }

  public Skill forName(String name) {
    if (skills.containsKey(name)) {
      return skills.get(name);
    }

    if ("Ball & Chain".equalsIgnoreCase(name) || "Ball &amp; Chain".equalsIgnoreCase(name)) {
      return skills.get("Ball and Chain");
    }
    return null;
  }
}
