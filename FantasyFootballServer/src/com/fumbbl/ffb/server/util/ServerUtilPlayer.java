package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;

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
		for (Player<?> offensiveAssist : offensiveAssists) {
			if (offensiveAssist != attacker) {
				FieldCoordinate coordinateAssist = game.getFieldModel().getPlayerCoordinate(offensiveAssist);
				Player<?>[] defensiveAssists = UtilPlayer.findAdjacentPlayersWithTacklezones(game, defenderTeam, coordinateAssist,
					false);
				// Check to see if the assisting player is not close to anyone else but the
				// defending blocker
				int defendingPlayersOtherThanBlocker = 0;
				for (Player<?> defensiveAssist : defensiveAssists) {
					if (defensiveAssist != defender)
						defendingPlayersOtherThanBlocker++;
				}

				boolean guardIsCanceled = game.getActingTeam().hasPlayer(attacker) && Arrays.stream(defensiveAssists)
					.flatMap(player -> player.getSkillsIncludingTemporaryOnes().stream())
					.anyMatch(skill -> skill.canCancel(NamedProperties.assistsBlocksInTacklezones));

				if ((offensiveAssist.hasSkillProperty(NamedProperties.assistsBlocksInTacklezones) && !guardIsCanceled)
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
