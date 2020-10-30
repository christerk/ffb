package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.PassingModifiers.PassContext;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilDisturbingPresence;
import com.balancedbytes.games.ffb.util.UtilPlayer;


/**
 * 
 * @author Kalimar
 */
public class PassModifierFactory implements IRollModifierFactory {
  
  public PassModifier forName(String pName) {
    for (PassModifier modifier : PassModifier.values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public Set<PassModifier> findPassModifiers(Game pGame, Player pThrower, PassingDistance pPassingDistance, boolean pThrowTeamMate) {
    Set<PassModifier> passModifiers = new HashSet<PassModifier>();
    if (pThrower != null) {
      if (Weather.VERY_SUNNY == pGame.getFieldModel().getWeather()) {
        passModifiers.add(PassingModifiers.VERY_SUNNY);
      }
      if (Weather.BLIZZARD == pGame.getFieldModel().getWeather()) {
        passModifiers.add(PassingModifiers.BLIZZARD);
      }

      if (!UtilCards.hasSkillWithProperty(pThrower, NamedProperties.ignoreTacklezonesWhenPassing)) {
        PassModifier tacklezoneModifier = getTacklezoneModifier(pGame, pThrower);
        if (tacklezoneModifier != null) {
          passModifiers.add(tacklezoneModifier);
        }
      }

      PassContext context = new PassContext(pPassingDistance, pThrowTeamMate);
      passModifiers.addAll(UtilCards.getPassModifiers(pThrower, context));
      
      
      if (UtilCards.hasCard(pGame, pThrower, Card.GROMSKULLS_EXPLODING_RUNES)) {
        passModifiers.add(PassModifier.GROMSKULLS_EXPLODING_RUNES);
      }
      PassModifier disturbingPresenceModifier = getDisturbingPresenceModifier(pGame, pThrower);
      if (disturbingPresenceModifier != null) {
        passModifiers.add(disturbingPresenceModifier);
      }
    }
    return passModifiers;
  }
  
  public PassModifier[] toArray(Set<PassModifier> pPassModifierSet) {
    if (pPassModifierSet != null) {
      PassModifier[] passModifierArray = pPassModifierSet.toArray(new PassModifier[pPassModifierSet.size()]);
      Arrays.sort(
        passModifierArray,
        new Comparator<PassModifier>() {
          public int compare(PassModifier pO1, PassModifier pO2) {
            return pO1.getName().compareTo(pO2.getName());
          }
        }
      );
      return passModifierArray;
    } else {
      return new PassModifier[0];
    }
  }

  private PassModifier getTacklezoneModifier(Game pGame, Player pPlayer) {
    int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
    for (PassModifier modifier : PassingModifiers.tackleZoneModifiers) {
      if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
        return modifier;
      }
    }
    return null;
  }
  
  private PassModifier getDisturbingPresenceModifier(Game pGame, Player pPlayer) {
    int disturbingPresences = UtilDisturbingPresence.findOpposingDisturbingPresences(pGame, pPlayer);
    for (PassModifier modifier : PassingModifiers.disturbingPresenceModifiers) {
      if (modifier.isDisturbingPresenceModifier() && (modifier.getModifier() == disturbingPresences)) {
        return modifier;
      }
    }
    return null;
  }

}
