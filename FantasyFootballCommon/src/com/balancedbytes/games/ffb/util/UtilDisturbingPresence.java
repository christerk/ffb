package com.balancedbytes.games.ffb.util;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.FieldCoordinateBounds;
import com.balancedbytes.games.ffb.model.FieldModel;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

/**
 * 
 * @author Kalimar
 */
public class UtilDisturbingPresence {

	public static int findOpposingDisturbingPresences(Game pGame, Player<?> pPlayer) {
		int disturbingPresences = 0;
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate playerCoordinate = fieldModel.getPlayerCoordinate(pPlayer);
		Team otherTeam = UtilPlayer.findOtherTeam(pGame, pPlayer);
		for (Player<?> opposingPlayer : otherTeam.getPlayers()) {
			FieldCoordinate coordinate = fieldModel.getPlayerCoordinate(opposingPlayer);
			if (opposingPlayer.hasSkillProperty(NamedProperties.inflictsDisturbingPresence)
					&& FieldCoordinateBounds.FIELD.isInBounds(coordinate)
					&& (playerCoordinate.distanceInSteps(coordinate) <= 3)) {
				// System.out.println(opposingPlayer.getName() + ": " +
				// playerCoordinate.distanceInSteps(coordinate));
				disturbingPresences++;
			}
		}
		return disturbingPresences;
	}

}
