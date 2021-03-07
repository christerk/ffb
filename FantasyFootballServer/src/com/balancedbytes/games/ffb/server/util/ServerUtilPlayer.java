package com.balancedbytes.games.ffb.server.util;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilPlayer;

public class ServerUtilPlayer {

	public static int findBlockStrength(Game game, Player<?> attacker, int attackerStrength, Player<?> defender) {
		Team defenderTeam = defender.getTeam();
		boolean flipOpponentIfSameTeam = attacker.hasSkillProperty(NamedProperties.flipSameTeamOpponentToOtherTeam);

		// team-mates assist b&c if attacker is on the same team as defender to gain
		// maximum block dice (more choice)
		// opposing teams tries to hinder
		if (flipOpponentIfSameTeam && (attacker.getTeam() == defender.getTeam())) {
			defenderTeam = UtilPlayer.findOtherTeam(game, defender);
		}
		int blockStrength = attackerStrength;
		FieldCoordinate coordinateDefender = game.getFieldModel().getPlayerCoordinate(defender);
		Player<?>[] offensiveAssists = UtilPlayer.findAdjacentPlayersWithTacklezones(game, attacker.getTeam(),
				coordinateDefender, false);
		for (int i = 0; i < offensiveAssists.length; i++) {
			if (offensiveAssists[i] != attacker) {
				FieldCoordinate coordinateAssist = game.getFieldModel().getPlayerCoordinate(offensiveAssists[i]);
				Player<?>[] defensiveAssists = UtilPlayer.findAdjacentPlayersWithTacklezones(game, defenderTeam, coordinateAssist,
						false);
				// Check to see if the assisting player is not close to anyone else but the
				// defending blocker
				int defendingPlayersOtherThanBlocker = 0;
				for (int y = 0; y < defensiveAssists.length; y++) {
					if (defensiveAssists[y] != defender)
						defendingPlayersOtherThanBlocker++;
				}

				if (offensiveAssists[i].hasSkillProperty(NamedProperties.assistInTacklezones)
						|| (defendingPlayersOtherThanBlocker == 0)) {
					// System.out.println(offensiveAssists[i].getName() + " assists " +
					// pAttacker.getName());
					blockStrength++;
				}
			}
		}
		return blockStrength;
	}

}
