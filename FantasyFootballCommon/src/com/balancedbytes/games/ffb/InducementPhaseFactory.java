package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class InducementPhaseFactory implements IEnumWithIdFactory, IEnumWithNameFactory {
  
  public InducementPhase forName(String pName) {
    if (StringTool.isProvided(pName)) {
      for (InducementPhase phase : InducementPhase.values()) {
        if (phase.getName().equalsIgnoreCase(pName)) {
          return phase;
        }
      }
      // backwards compatibility (name change)
      if ("afterKickoffToOpponentResolved".equals(pName)) {
        return InducementPhase.AFTER_KICKOFF_TO_OPPONENT;
      }
    }
    return null;
  }

  public InducementPhase forId(int pId) {
    if (pId > 0) {
      for (InducementPhase phase : InducementPhase.values()) {
        if (phase.getId() == pId) {
          return phase;
        }
      }
    }
    return null;
  }
  
  public String getDescription(InducementPhase[] pPhases) {
    StringBuilder description = new StringBuilder();
    description.append("Play ");
    boolean firstPhase = true;
    for (InducementPhase phase : pPhases) {
      if (firstPhase) {
        firstPhase = false;
      } else {
        description.append(" or ");
      }
      description.append(phase.getDescription());
    }
    return description.toString();
  }

}
