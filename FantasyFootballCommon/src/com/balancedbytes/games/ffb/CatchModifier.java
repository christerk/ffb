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
public enum CatchModifier implements IRollModifier {
  
  ACCURATE(1, "Accurate Pass", -1, false, false),
  NERVES_OF_STEEL(2, "Nerves of Steel", 0, false, false),
  EXTRA_ARMS(3, "Extra Arms", -1, false, false),
  POURING_RAIN(4, "Pouring Rain", 1, false, false),
  TACKLEZONES_1(5, "1 Tacklezone", 1, true, false),
  TACKLEZONES_2(6, "2 Tacklezones", 2, true, false),
  TACKLEZONES_3(7, "3 Tacklezones", 3, true, false),
  TACKLEZONES_4(8, "4 Tacklezones", 4, true, false),
  TACKLEZONES_5(9, "5 Tacklezones", 5, true, false),
  TACKLEZONES_6(10, "6 Tacklezones", 6, true, false),
  TACKLEZONES_7(11, "7 Tacklezones", 7, true, false),
  TACKLEZONES_8(12, "8 Tacklezones", 8, true, false),
  DISTURBING_PRESENCES_1(13, "1 Disturbing Presence", 1, false, true),
  DISTURBING_PRESENCES_2(14, "2 Disturbing Presences", 2, false, true),
  DISTURBING_PRESENCES_3(15, "3 Disturbing Presences", 3, false, true),
  DISTURBING_PRESENCES_4(16, "4 Disturbing Presences", 4, false, true),
  DISTURBING_PRESENCES_5(17, "5 Disturbing Presences", 5, false, true),
  DISTURBING_PRESENCES_6(18, "6 Disturbing Presences", 6, false, true),
  DISTURBING_PRESENCES_7(19, "7 Disturbing Presences", 7, false, true),
  DISTURBING_PRESENCES_8(20, "8 Disturbing Presences", 8, false, true),
  DISTURBING_PRESENCES_9(21, "9 Disturbing Presences", 9, false, true),
  DISTURBING_PRESENCES_10(22, "10 Disturbing Presences", 10, false, true),
  DISTURBING_PRESENCES_11(23, "11 Disturbing Presences", 11, false, true),
  DIVING_CATCH(24, "Diving Catch", -1, false, false),
  HAND_OFF(25, "Hand Off", -1, false, false);

  private int fId;
  private String fName;
  private int fModifier;
  private boolean fTacklezoneModifier;
  private boolean fDisturbingPresenceModifier;
  
  private CatchModifier(int pId, String pName, int pModifier, boolean pTacklezoneModifier, boolean pDisturbingPresenceModifier) {
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
  
  public static CatchModifier fromId(int pId) {
    for (CatchModifier modifier : values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public static CatchModifier fromName(String pName) {
    for (CatchModifier modifier : values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public static Set<CatchModifier> findCatchModifiers(Game pGame, Player pPlayer, CatchScatterThrowInMode pCatchMode) {
    Set<CatchModifier> catchModifiers = new HashSet<CatchModifier>();
    if ((CatchScatterThrowInMode.CATCH_ACCURATE_PASS == pCatchMode) || (CatchScatterThrowInMode.CATCH_ACCURATE_BOMB == pCatchMode)) {
      catchModifiers.add(CatchModifier.ACCURATE);
      if (UtilCards.hasSkill(pGame, pPlayer, Skill.DIVING_CATCH)) {
        catchModifiers.add(CatchModifier.DIVING_CATCH);
      }
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
      CatchModifier tacklezoneModifier = CatchModifier.getTacklezoneModifier(pGame, pPlayer);
      if (tacklezoneModifier != null) {
        catchModifiers.add(tacklezoneModifier);
      }
    }
    CatchModifier disturbingPresenceModifier = CatchModifier.getDisturbingPresenceModifier(pGame, pPlayer);
    if (disturbingPresenceModifier != null) {
      catchModifiers.add(disturbingPresenceModifier);
    }
    return catchModifiers;
  }
  
  public static CatchModifier[] toArray(Set<CatchModifier> pCatchModifierSet) {
    if (pCatchModifierSet != null) {
      CatchModifier[] catchModifierArray = pCatchModifierSet.toArray(new CatchModifier[pCatchModifierSet.size()]);
      Arrays.sort(
        catchModifierArray,
        new Comparator<CatchModifier>() {
          public int compare(CatchModifier pO1, CatchModifier pO2) {
            return (pO1.getId() - pO2.getId());
          }
        }
      );
      return catchModifierArray;
    } else {
      return new CatchModifier[0];
    }
  }
  
  private static CatchModifier getTacklezoneModifier(Game pGame, Player pPlayer) {
    int tacklezones = UtilPlayer.findTacklezones(pGame, pPlayer);
    if (tacklezones > 0) {
      for (CatchModifier modifier : values()) {
        if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
          return modifier;
        }
      }
    }
    return null;
  }
  
  private static CatchModifier getDisturbingPresenceModifier(Game pGame, Player pPlayer) {
    int disturbingPresences = UtilDisturbingPresence.findOpposingDisturbingPresences(pGame, pPlayer);
    if (disturbingPresences > 0) {
      for (CatchModifier modifier : values()) {
        if (modifier.isDisturbingPresenceModifier() && (modifier.getModifier() == disturbingPresences)) {
          return modifier;
        }
      }
    }
    return null;
  }
  
  public boolean isModifierIncluded() {
    return (isTacklezoneModifier() || isDisturbingPresenceModifier());
  }
  
}
