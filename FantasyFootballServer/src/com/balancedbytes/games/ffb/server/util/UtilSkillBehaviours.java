package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;

public class UtilSkillBehaviours {

  public static void RegisterBehaviours(SkillFactory factory) {

    String behaviourPackage = "com.balancedbytes.games.ffb.server.skillbehaviour";
    for (Skill skill : factory.getSkills()) {
      String skillClassName = skill.getClass().getSimpleName();

      try {
        Class<?> behaviourClass = Class.forName(behaviourPackage+"."+skillClassName + "Behaviour");
        registerBehaviour((SkillBehaviour<?>) behaviourClass.getConstructor((Class<?>[])null).newInstance((Object[])null), factory);
      } catch (Exception e) { }
    }
  }

  private static void registerBehaviour(SkillBehaviour<?> behaviour, SkillFactory factory) {
    Skill skill = factory.forClass(behaviour.skillClass);
    behaviour.setSkill(skill);
  }
}
