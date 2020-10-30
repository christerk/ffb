package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

/**
 * 
 * @author Kalimar
 */
public class DodgeModifierFactory implements IRollModifierFactory {
  
  public DodgeModifier forName(String pName) {
    for (DodgeModifier modifier : DodgeModifier.values()) {
      if (modifier.getName().equalsIgnoreCase(pName)) {
        return modifier;
      }
    }
    return null;
  }

  public Set<DodgeModifier> findDodgeModifiers(Game pGame, FieldCoordinate pCoordinateFrom, FieldCoordinate pCoordinateTo, int pTacklezoneModifier) {
    Set<DodgeModifier> dodgeModifiers = new HashSet<DodgeModifier>();
    ActingPlayer actingPlayer = pGame.getActingPlayer();
    if (UtilCards.hasSkill(pGame, actingPlayer, Skill.TWO_HEADS)) {
      dodgeModifiers.add(DodgeModifier.TWO_HEADS);
    }
    if (UtilCards.hasSkill(pGame, actingPlayer, Skill.TITCHY)) {
      dodgeModifiers.add(DodgeModifier.TITCHY);
    }
    DodgeModifier prehensileTailModifier = findPrehensileTailModifier(pGame, pCoordinateFrom);
    if (prehensileTailModifier != null) {
      dodgeModifiers.add(prehensileTailModifier);
    }
    DodgeModifier tacklezoneModifier = findTacklezoneModifier(pGame, pCoordinateTo, pTacklezoneModifier);
    if (tacklezoneModifier != null) {
    	boolean hasStunty = UtilCards.hasSkill(pGame, actingPlayer, Skill.STUNTY);
    	boolean hasSecretWeapon = UtilCards.hasSkill(pGame, actingPlayer, Skill.SECRET_WEAPON);
    	boolean hasSwoop = UtilCards.hasSkill(pGame, actingPlayer, Skill.SWOOP);
      if (hasStunty && !hasSecretWeapon && !hasSwoop) {
        dodgeModifiers.add(DodgeModifier.STUNTY);
      } else {
        dodgeModifiers.add(tacklezoneModifier);
      }
    }
    if (UtilCards.hasUnusedSkill(pGame, actingPlayer, Skill.BREAK_TACKLE)) {
      dodgeModifiers.add(DodgeModifier.BREAK_TACKLE);
    }
    return dodgeModifiers;
  }
    
  public DodgeModifier[] toArray(Set<DodgeModifier> pDodgeModifierSet) {
    if (pDodgeModifierSet != null) {
      DodgeModifier[] dodgeModifierArray = pDodgeModifierSet.toArray(new DodgeModifier[pDodgeModifierSet.size()]);
      Arrays.sort(
          dodgeModifierArray,
        new Comparator<DodgeModifier>() {
          public int compare(DodgeModifier pO1, DodgeModifier pO2) {
            return pO1.getName().compareTo(pO2.getName());
          }
        }
      );
      return dodgeModifierArray;
    } else {
      return new DodgeModifier[0];
    }
  }
  
  private DodgeModifier findTacklezoneModifier(Game pGame, FieldCoordinate pCoordinateTo, int pModifier) {
    ActingPlayer actingPlayer = pGame.getActingPlayer();
    Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
    int tacklezones = pModifier;
    Player[] adjacentPlayers = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, pCoordinateTo, false);
    for (Player player : adjacentPlayers) {
      if (!UtilCards.hasSkill(pGame, player, Skill.TITCHY)) {
        tacklezones++;
      }
    }
    for (DodgeModifier modifier : DodgeModifier.values()) {
      if (modifier.isTacklezoneModifier() && (modifier.getModifier() == tacklezones)) {
        return modifier;
      }
    }
    return null;
  }
  
  private DodgeModifier findPrehensileTailModifier(Game pGame, FieldCoordinate pCoordinateFrom) {
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
      for (DodgeModifier modifier : DodgeModifier.values()) {
        if (modifier.isPrehensileTailModifier() && (modifier.getModifier() == nrOfPrehensileTails)) {
          return modifier;
        }
      }
    }
    return null;
  }
  
}
