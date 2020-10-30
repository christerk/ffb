package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.util.UtilCards;


/**
 * 
 * @author Kalimar
 */
public class LeapModifierFactory implements IRollModifierFactory {
  
  public LeapModifier forName(String pName) {
    for (LeapModifier modifier : LeapModifier.values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public Set<LeapModifier> findLeapModifiers(Game pGame) {
    Set<LeapModifier> leapModifiers = new HashSet<LeapModifier>();
    ActingPlayer actingPlayer = pGame.getActingPlayer();
    if (UtilCards.hasSkill(pGame, actingPlayer, Skill.VERY_LONG_LEGS)) {
      leapModifiers.add(LeapModifier.VERY_LONG_LEGS);
    }
    return leapModifiers;
  }
  
  public LeapModifier[] toArray(Set<LeapModifier> pLeapModifierSet) {
    if (pLeapModifierSet != null) {
      LeapModifier[] leapModifierArray = pLeapModifierSet.toArray(new LeapModifier[pLeapModifierSet.size()]);
      Arrays.sort(
        leapModifierArray,
        new Comparator<LeapModifier>() {
          public int compare(LeapModifier pO1, LeapModifier pO2) {
            return pO1.getName().compareTo(pO2.getName());
          }
        }
      );
      return leapModifierArray;
    } else {
      return new LeapModifier[0];
    }
  }
  
}
