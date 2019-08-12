package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilDisturbingPresence;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class CatchModifierFactory implements IRollModifierFactory {
  
  public CatchModifier forName(String pName) {
    for (CatchModifier modifier : CatchModifier.values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public Set<CatchModifier> findCatchModifiers(Game pGame, Player pPlayer, CatchScatterThrowInMode pCatchMode) {
    Set<CatchModifier> catchModifiers = new HashSet<CatchModifier>();
    if ((CatchScatterThrowInMode.CATCH_ACCURATE_PASS == pCatchMode) || (CatchScatterThrowInMode.CATCH_ACCURATE_BOMB == pCatchMode)) {
      catchModifiers.add(CatchModifier.ACCURATE);
      if (UtilCards.hasSkill(pGame, pPlayer, Skill.DIVING_CATCH)) {
        catchModifiers.add(CatchModifier.DIVING_CATCH);
      }
    }
    if ((CatchScatterThrowInMode.CATCH_ACCURATE_PASS_EMPTY_SQUARE == pCatchMode || CatchScatterThrowInMode.CATCH_ACCURATE_BOMB_EMPTY_SQUARE == pCatchMode)
      && UtilCards.hasSkill(pGame, pPlayer, Skill.DIVING_CATCH)) {
      catchModifiers.add(CatchModifier.ACCURATE);
    }
    if (CatchScatterThrowInMode.CATCH_HAND_OFF == pCatchMode) {
      catchModifiers.add(CatchModifier.HAND_OFF);
    }
    if (Weather.POURING_RAIN == pGame.getFieldModel().getWeather()) {
      catchModifiers.add(CatchModifier.POURING_RAIN);
    }
    if (UtilCards.hasSkill(pGame, pPlayer, Skill.EXTRA_ARMS)) {
      catchModifiers.add(CatchModifier.EXTRA_ARMS);
    }
    if (UtilCards.hasSkill(pGame, pPlayer, Skill.NERVES_OF_STEEL)) {
      catchModifiers.add(CatchModifier.NERVES_OF_STEEL);
    } else {
      CatchModifier tacklezoneModifier = getTacklezoneModifier(pGame, pPlayer);
      if (tacklezoneModifier != null) {
        catchModifiers.add(tacklezoneModifier);
      }
    }
    CatchModifier disturbingPresenceModifier = getDisturbingPresenceModifier(pGame, pPlayer);
    if (disturbingPresenceModifier != null) {
      catchModifiers.add(disturbingPresenceModifier);
    }
    return catchModifiers;
  }
  
  public CatchModifier[] toArray(Set<CatchModifier> pCatchModifierSet) {
    if (pCatchModifierSet != null) {
      CatchModifier[] catchModifierArray = pCatchModifierSet.toArray(new CatchModifier[pCatchModifierSet.size()]);
      Arrays.sort(
        catchModifierArray,
        new Comparator<CatchModifier>() {
          public int compare(CatchModifier pO1, CatchModifier pO2) {
            return pO1.getName().compareTo(pO2.getName());
          }
        }
      );
      return catchModifierArray;
    } else {
      return new CatchModifier[0];
    }
  }
  
  private CatchModifier getTacklezoneModifier(Game pGame, Player pPlayer) {
    int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
    if (tacklezones > 0) {
      for (CatchModifier modifier : CatchModifier.values()) {
        if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
          return modifier;
        }
      }
    }
    return null;
  }
  
  private CatchModifier getDisturbingPresenceModifier(Game pGame, Player pPlayer) {
    int disturbingPresences = UtilDisturbingPresence.findOpposingDisturbingPresences(pGame, pPlayer);
    if (disturbingPresences > 0) {
      for (CatchModifier modifier : CatchModifier.values()) {
        if (modifier.isDisturbingPresenceModifier() && (modifier.getModifier() == disturbingPresences)) {
          return modifier;
        }
      }
    }
    return null;
  }
  
}
