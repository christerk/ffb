package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class ReRolledActionFactory implements INamedObjectFactory {
  
  public ReRolledAction forName(String pName) {
    for (ReRolledAction action : ReRolledAction.values()) {
      if (action.getName().equalsIgnoreCase(pName)) {
        return action;
      }
    }
    return null;
  }

  public ReRolledAction forSkill(Skill pSkill) {
    for (ReRolledAction action : ReRolledAction.values()) {
      if (pSkill == action.getSkill()) {
        return action;
      }
    }
    return null;
  }

}
