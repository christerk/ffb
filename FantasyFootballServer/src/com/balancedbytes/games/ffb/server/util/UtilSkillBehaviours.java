package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.server.DebugLog;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;

import java.lang.reflect.InvocationTargetException;

public class UtilSkillBehaviours {

  public static void registerBehaviours(SkillFactory factory, DebugLog log) {

    String behaviourPackage = "com.balancedbytes.games.ffb.server.skillbehaviour";
    for (Skill skill : factory.getSkills()) {
      String skillClassName = skill.getClass().getSimpleName();

      try {
        Class<?> behaviourClass = Class.forName(behaviourPackage + "." + skillClassName + "Behaviour");
        @SuppressWarnings("unchecked") SkillBehaviour<Skill> behaviour = (SkillBehaviour<Skill>) behaviourClass.getConstructor((Class<Skill>[])null).newInstance((Object[])null);
        if (registerBehaviour(behaviour, factory)) {
          log.log(IServerLogLevel.DEBUG, "Registered behavior class '" + behaviourClass.getSimpleName() + "' for skill '" + skillClassName + "'");
        } else {
          log.log(IServerLogLevel.WARN, "No skill found for '" + behaviour.getClass().getSimpleName());
        }
      } catch (ClassNotFoundException e) {
        log.log(IServerLogLevel.DEBUG, "No behavior found for '" + skillClassName + "'");
      } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
        log.log(IServerLogLevel.WARN, "Failed to register behaviour for '" + skillClassName + "': " + e.getMessage());
      }
    }
  }

  private static boolean registerBehaviour(SkillBehaviour<Skill> behaviour, SkillFactory factory) {
    Skill skill = factory.forClass(behaviour.skillClass);
    if (skill != null) {
      behaviour.setSkill(skill);
      return true;
    }
    return false;
  }
}
