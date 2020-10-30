package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;

public class UtilSkillBehaviours {
  private static SkillFactory factory;

  @SuppressWarnings("unchecked")
  public static void RegisterBehaviours(SkillFactory factory) {
    UtilSkillBehaviours.factory = factory;

    String behaviourPackage = "com.balancedbytes.games.ffb.server.skillbehaviour";
    for (Skill skill : factory.getSkills()) {
      String skillClassName = skill.getClass().getSimpleName();

      try {
        Class behaviourClass = Class.forName(behaviourPackage+"."+skillClassName + "Behaviour");
        registerBehaviour((SkillBehaviour<?>) behaviourClass.getConstructor((Class[])null).newInstance((Object[])null));
      } catch (Exception e) { }
    }
  }

  private static void registerBehaviour(SkillBehaviour<?> behaviour) {
    
    Skill skill = factory.forClass(behaviour.skillClass);
    behaviour.setSkill(skill);
  }
}
