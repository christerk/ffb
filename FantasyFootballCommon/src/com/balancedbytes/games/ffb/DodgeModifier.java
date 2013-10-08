package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public enum DodgeModifier implements IRollModifier {
  
  STUNTY(1, "Stunty", 0, false, false),
  BREAK_TACKLE(2, "Break Tackle", 0, false, false),
  TWO_HEADS(3, "Two Heads", -1, false, false),
  DIVING_TACKLE(4, "Diving Tackle", 2, false, false),
  TITCHY(5, "Titchy", -1, false, false),
  TACKLEZONES_1(6, "1 Tacklezone", 1, true, false),
  TACKLEZONES_2(7, "2 Tacklezones", 2, true, false),
  TACKLEZONES_3(8, "3 Tacklezones", 3, true, false),
  TACKLEZONES_4(9, "4 Tacklezones", 4, true, false),
  TACKLEZONES_5(10, "5 Tacklezones", 5, true, false),
  TACKLEZONES_6(11, "6 Tacklezones", 6, true, false),
  TACKLEZONES_7(12, "7 Tacklezones", 7, true, false),
  TACKLEZONES_8(13, "8 Tacklezones", 8, true, false),
  PREHENSILE_TAIL_1(14, "1 Prehensile Tail", 1, false, true),
  PREHENSILE_TAIL_2(15, "2 Prehensile Tails", 2, false, true),
  PREHENSILE_TAIL_3(16, "3 Prehensile Tails", 3, false, true),
  PREHENSILE_TAIL_4(17, "4 Prehensile Tails", 4, false, true),
  PREHENSILE_TAIL_5(18, "5 Prehensile Tails", 5, false, true),
  PREHENSILE_TAIL_6(19, "6 Prehensile Tails", 6, false, true),
  PREHENSILE_TAIL_7(20, "7 Prehensile Tails", 7, false, true),
  PREHENSILE_TAIL_8(21, "8 Prehensile Tails", 8, false, true);
  
  private int fId;
  private String fName;
  private int fModifier;
  private boolean fTacklezoneModifier;
  private boolean fPrehensileTailModifier;
  
  private DodgeModifier(int pId, String pName, int pModifier, boolean pTacklezoneModifier, boolean pPrehensileTailModifier) {
    fId = pId;
    fName = pName;
    fModifier = pModifier;
    fTacklezoneModifier = pTacklezoneModifier;
    fPrehensileTailModifier = pPrehensileTailModifier;
  }
  
  public int getId() {
    return fId;
  }
  
  public int getModifier() {
    return fModifier;
  }
  
  public String getName() {
    return fName;
  }

  public boolean isTacklezoneModifier() {
    return fTacklezoneModifier;
  }
  
  public boolean isPrehensileTailModifier() {
    return fPrehensileTailModifier;
  }
  
  public static DodgeModifier fromId(int pId) {
    for (DodgeModifier modifier : values()) {
      if (modifier.getId() == pId) {
        return modifier;
      }
    }
    return null;
  }
  
  public static DodgeModifier fromName(String pName) {
    for (DodgeModifier modifier : values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public static Set<DodgeModifier> findDodgeModifiers(Game pGame, FieldCoordinate pCoordinateFrom, FieldCoordinate pCoordinateTo, int pTacklezoneModifier) {
    Set<DodgeModifier> dodgeModifiers = new HashSet<DodgeModifier>();
    ActingPlayer actingPlayer = pGame.getActingPlayer();
    if (UtilCards.hasSkill(pGame, actingPlayer, Skill.TWO_HEADS)) {
      dodgeModifiers.add(TWO_HEADS);
    }
    if (UtilCards.hasSkill(pGame, actingPlayer, Skill.TITCHY)) {
      dodgeModifiers.add(TITCHY);
    }
    DodgeModifier prehensileTailModifier = findPrehensileTailModifier(pGame, pCoordinateFrom);
    if (prehensileTailModifier != null) {
      dodgeModifiers.add(prehensileTailModifier);
    }
    DodgeModifier tacklezoneModifier = findTacklezoneModifier(pGame, pCoordinateTo, pTacklezoneModifier);
    if (tacklezoneModifier != null) {
      if (UtilCards.hasSkill(pGame, actingPlayer, Skill.STUNTY) && (!UtilCards.hasSkill(pGame, actingPlayer, Skill.SECRET_WEAPON))) {
        dodgeModifiers.add(STUNTY);
      } else {
        dodgeModifiers.add(tacklezoneModifier);
      }
    }
    if (UtilCards.hasUnusedSkill(pGame, actingPlayer, Skill.BREAK_TACKLE)) {
      dodgeModifiers.add(BREAK_TACKLE);
    }
    return dodgeModifiers;
  }
    
  public static DodgeModifier[] toArray(Set<DodgeModifier> pDodgeModifierSet) {
    if (pDodgeModifierSet != null) {
      DodgeModifier[] dodgeModifierArray = pDodgeModifierSet.toArray(new DodgeModifier[pDodgeModifierSet.size()]);
      Arrays.sort(
          dodgeModifierArray,
        new Comparator<DodgeModifier>() {
          public int compare(DodgeModifier pO1, DodgeModifier pO2) {
            return (pO1.getId() - pO2.getId());
          }
        }
      );
      return dodgeModifierArray;
    } else {
      return new DodgeModifier[0];
    }
  }
  
  private static DodgeModifier findTacklezoneModifier(Game pGame, FieldCoordinate pCoordinateTo, int pModifier) {
    ActingPlayer actingPlayer = pGame.getActingPlayer();
    Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
    int tacklezones = pModifier;
    Player[] adjacentPlayers = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, pCoordinateTo, false);
    for (Player player : adjacentPlayers) {
      if (!UtilCards.hasSkill(pGame, player, Skill.TITCHY)) {
        tacklezones++;
      }
    }
    for (DodgeModifier modifier : values()) {
      if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
        return modifier;
      }
    }
    return null;
  }
  
  private static DodgeModifier findPrehensileTailModifier(Game pGame, FieldCoordinate pCoordinateFrom) {
    ActingPlayer actingPlayer = pGame.getActingPlayer();
    Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
    int nrOfPrehensileTails = 0;
    Player[] opponents = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, pCoordinateFrom, true);
    for (Player opponent : opponents) {
      if (UtilCards.hasSkill(pGame, opponent, Skill.PREHENSILE_TAIL)) {
        nrOfPrehensileTails++;
      }
    }
    if (nrOfPrehensileTails > 0) {
      for (DodgeModifier modifier : values()) {
        if (modifier.isPrehensileTailModifier() && (modifier.getModifier() == nrOfPrehensileTails)) {
          return modifier;
        }
      }
    }
    return null;
  }
  
  public boolean isModifierIncluded() {
    return (isTacklezoneModifier() || isPrehensileTailModifier());
  }

}
