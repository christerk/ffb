package com.balancedbytes.games.ffb;

/**
 * 
 * @author Kalimar
 */
public class ReRolledActionFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public ReRolledAction forName(String pName) {
    for (ReRolledAction action : ReRolledAction.values()) {
      if (action.getName().equalsIgnoreCase(pName)) {
        return action;
      }
    }
    return null;
  }

  public ReRolledAction forId(int pId) {
    for (ReRolledAction action : ReRolledAction.values()) {
      if (pId == action.getId()) {
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
