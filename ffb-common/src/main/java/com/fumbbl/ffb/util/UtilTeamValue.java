package com.fumbbl.ffb.util;

import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Position;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;

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
				for (Player<?> player : pTeam.getPlayers()) {
					if (player.getRecoveringInjury() == null) {
						teamValue += findPlayerValue(player);
					}
				}
			}
		}
		return teamValue;
	}

	private static int findPlayerValue(Player<?> pPlayer) {
		int playerValue = 0;
		if (pPlayer != null) {
			Position position = pPlayer.getPosition();
			if (position != null) {
				playerValue += position.getCost();
				for (Skill skill : pPlayer.getSkills()) {
					playerValue += skill.getCost(pPlayer);
				}
			}
		}
		return playerValue;
	}

}
