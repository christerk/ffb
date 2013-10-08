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
public enum PassModifier implements IRollModifier {
  
  ACCURATE(1, "Accurate", -1, false, false),
  NERVES_OF_STEEL(2, "Nerves of Steel", 0, false, false),
  STRONG_ARM(3, "Strong Arm", -1, false, false),
  VERY_SUNNY(4, "Very Sunny", 1, false, false),
  BLIZZARD(5, "Blizzard", 0, false, false),
  STUNTY(6, "Stunty", 1, false, false),
  TACKLEZONES_1(7, "1 Tacklezone", 1, true, false),
  TACKLEZONES_2(8, "2 Tacklezones", 2, true, false),
  TACKLEZONES_3(9, "3 Tacklezones", 3, true, false),
  TACKLEZONES_4(10, "4 Tacklezones", 4, true, false),
  TACKLEZONES_5(11, "5 Tacklezones", 5, true, false),
  TACKLEZONES_6(12, "6 Tacklezones", 6, true, false),
  TACKLEZONES_7(13, "7 Tacklezones", 7, true, false),
  TACKLEZONES_8(14, "8 Tacklezones", 8, true, false),
  DISTURBING_PRESENCES_1(15, "1 Disturbing Presence", 1, false, true),
  DISTURBING_PRESENCES_2(16, "2 Disturbing Presences", 2, false, true),
  DISTURBING_PRESENCES_3(17, "3 Disturbing Presences", 3, false, true),
  DISTURBING_PRESENCES_4(18, "4 Disturbing Presences", 4, false, true),
  DISTURBING_PRESENCES_5(19, "5 Disturbing Presences", 5, false, true),
  DISTURBING_PRESENCES_6(20, "6 Disturbing Presences", 6, false, true),
  DISTURBING_PRESENCES_7(21, "7 Disturbing Presences", 7, false, true),
  DISTURBING_PRESENCES_8(22, "8 Disturbing Presences", 8, false, true),
  DISTURBING_PRESENCES_9(23, "9 Disturbing Presences", 9, false, true),
  DISTURBING_PRESENCES_10(24, "10 Disturbing Presences", 10, false, true),
  DISTURBING_PRESENCES_11(25, "11 Disturbing Presences", 11, false, true),
  THROW_TEAM_MATE(26, "Throw Team-Mate", 1, false, false);

  private int fId;
  private String fName;
  private int fModifier;
  private boolean fTacklezoneModifier;
  private boolean fDisturbingPresenceModifier;
  
  private PassModifier(int pId, String pName, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
    fId = pId;
    fName = pName;
    fModifier = pModifier;
    fTacklezoneModifier = pTacklezoneModifier;
    fDisturbingPresenceModifier = pDisturbingPresenceModifier;
  }
  
  public String getName() {
    return fName;
  }
  
  public int getId() {
    return fId;
  }
  
  public int getModifier() {
    return fModifier;
  }
  
  public boolean isTacklezoneModifier() {
    return fTacklezoneModifier;
  }
  
  public boolean isDisturbingPresenceModifier() {
    return fDisturbingPresenceModifier;
  }
  
  public static PassModifier fromId(int pId) {
    for (PassModifier modifier : values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public static PassModifier fromName(String pName) {
    for (PassModifier modifier : values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }
  
  public static Set<PassModifier> findPassModifiers(Game pGame, Player pThrower, PassingDistance pPassingDistance, boolean pThrowTeamMate) {
    Set<PassModifier> passModifiers = new HashSet<PassModifier>();
    if (pThrower != null) {
    	if (pThrowTeamMate) {
    		passModifiers.add(PassModifier.THROW_TEAM_MATE);
    	}
      if (Weather.VERY_SUNNY == pGame.getFieldModel().getWeather()) {
        passModifiers.add(PassModifier.VERY_SUNNY);
      }
      if (Weather.BLIZZARD == pGame.getFieldModel().getWeather()) {
        passModifiers.add(PassModifier.BLIZZARD);
      }
      if (UtilCards.hasSkill(pGame, pThrower, Skill.NERVES_OF_STEEL)) {
        passModifiers.add(PassModifier.NERVES_OF_STEEL);
      } else {
        PassModifier tacklezoneModifier = PassModifier.getTacklezoneModifier(pGame, pThrower);
        if (tacklezoneModifier != null) {
          passModifiers.add(tacklezoneModifier);
        }
      }
      if (UtilCards.hasSkill(pGame, pThrower, Skill.STRONG_ARM) && (pPassingDistance != PassingDistance.QUICK_PASS)) {
        passModifiers.add(PassModifier.STRONG_ARM);
      }
      if (UtilCards.hasSkill(pGame, pThrower, Skill.STUNTY)) {
        passModifiers.add(PassModifier.STUNTY);
      }
      if (UtilCards.hasSkill(pGame, pThrower, Skill.ACCURATE)) {
        passModifiers.add(PassModifier.ACCURATE);
      }
      PassModifier disturbingPresenceModifier = PassModifier.getDisturbingPresenceModifier(pGame, pThrower);
      if (disturbingPresenceModifier != null) {
        passModifiers.add(disturbingPresenceModifier);
      }
    }
    return passModifiers;
  }
  
  public static PassModifier[] toArray(Set<PassModifier> pPassModifierSet) {
    if (pPassModifierSet != null) {
      PassModifier[] passModifierArray = pPassModifierSet.toArray(new PassModifier[pPassModifierSet.size()]);
      Arrays.sort(
        passModifierArray,
        new Comparator<PassModifier>() {
          public int compare(PassModifier pO1, PassModifier pO2) {
            return (pO1.getId() - pO2.getId());
          }
        }
      );
      return passModifierArray;
    } else {
      return new PassModifier[0];
    }
  }

  private static PassModifier getTacklezoneModifier(Game pGame, Player pPlayer) {
    int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
    for (PassModifier modifier : values()) {
      if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
        return modifier;
      }
    }
    return null;
  }
  
  private static PassModifier getDisturbingPresenceModifier(Game pGame, Player pPlayer) {
    int disturbingPresences = UtilDisturbingPresence.findOpposingDisturbingPresences(pGame, pPlayer);
    for (PassModifier modifier : values()) {
      if (modifier.isDisturbingPresenceModifier() && (modifier.getModifier() == disturbingPresences)) {
        return modifier;
      }
    }
    return null;
  }
  
  public boolean isModifierIncluded() {
    return (isTacklezoneModifier() || isDisturbingPresenceModifier());
  }
    
}
