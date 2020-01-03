package com.balancedbytes.games.ffb.util;

import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Position;
import com.balancedbytes.games.ffb.model.Roster;
import com.balancedbytes.games.ffb.model.Team;

/**
 * 
 * @author Kalimar
 */
public class UtilTeamValue {
  
  public static int findTeamValue(Team pTeam) {
    int teamValue = 0;
    if (pTeam != null) {
      Roster roster = pTeam.getRoster();
      if (roster != null) {
        teamValue += pTeam.getReRolls() * roster.getReRollCost();
        teamValue += pTeam.getFanFactor() * 10000;
        teamValue += pTeam.getAssistantCoaches() * 10000;
        teamValue += pTeam.getCheerleaders() * 10000;
        teamValue += pTeam.getApothecaries() * 50000;
        for (Player player : pTeam.getPlayers()) {
          if (player.getRecoveringInjury() == null) {
            teamValue += findPlayerValue(player);
          }
        }
      }
    }
    return teamValue;
  }

  private static int findPlayerValue(Player pPlayer) {
    int playerValue = 0;
    if (pPlayer != null) {
      Position position = pPlayer.getPosition();
      if (position != null) {
        playerValue += position.getCost();
        for (Skill skill : pPlayer.getSkills()) {
          if (!position.hasSkill(skill) && (skill != Skill.LONER)) {
            switch (skill) {
              case AGILITY_INCREASE:
                playerValue += 40000;
                break;
              case STRENGTH_INCREASE:
                playerValue += 50000;
                break;
              case MOVEMENT_INCREASE:
              case ARMOUR_INCREASE:
                playerValue += 30000;
                break;
              default:
                if (position.isDoubleCategory(skill.getCategory())) {
                  playerValue += 30000;
                } else {
                  playerValue += 20000;
                }
                break;
            }
          }
        }
      }
    }
    return playerValue;
  }
  
}
