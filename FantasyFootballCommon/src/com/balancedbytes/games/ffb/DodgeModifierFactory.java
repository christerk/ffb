package com.balancedbytes.games.ffb;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.balancedbytes.games.ffb.DodgeModifiers.DodgeContext;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
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
    
    DodgeContext context = new DodgeContext(actingPlayer, pCoordinateFrom);
    
    dodgeModifiers.addAll(UtilCards.getDodgeModifiers(actingPlayer, context));
    
    DodgeModifier tacklezoneModifier = findTacklezoneModifier(pGame, pCoordinateTo, pTacklezoneModifier);
    
    boolean preventStunty = UtilCards.hasSkillWithProperty(actingPlayer.getPlayer(), NamedProperties.preventStuntyDodgeModifier);

    if (context.addTackleZoneModifier || preventStunty) {
      dodgeModifiers.add(tacklezoneModifier);
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
      if (!UtilCards.hasSkillWithProperty(player, NamedProperties.hasNoTacklezone)) {
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
      if (UtilCards.hasSkill(pGame, opponent, SkillConstants.PREHENSILE_TAIL)) {
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
