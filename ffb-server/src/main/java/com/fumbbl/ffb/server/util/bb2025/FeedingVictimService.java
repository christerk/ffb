package com.fumbbl.ffb.server.util.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Determines which players a player suffering from Bloodlust may bite: adjacent Thrall team-mates and,
 * with Tasty Morsel, adjacent non-Star opponents with ST 3 or lower.
 */
public class FeedingVictimService {

	private static final int MAX_OPPONENT_STRENGTH = 3;

	public List<Player<?>> findVictims(Game game, Team team, FieldCoordinate coordinate, boolean canBiteOpponents) {
		List<Player<?>> victims = new ArrayList<>(Arrays.asList(UtilPlayer.findAdjacentPlayersToFeedOn(game, team, coordinate)));
		if (canBiteOpponents) {
			victims.addAll(findOpponentsToFeedOn(game, team, coordinate));
		}
		return victims;
	}

	private List<Player<?>> findOpponentsToFeedOn(Game game, Team team, FieldCoordinate coordinate) {
		List<Player<?>> opponents = new ArrayList<>();
		FieldModel fieldModel = game.getFieldModel();
		FieldCoordinate[] adjacentCoordinates = fieldModel.findAdjacentCoordinates(coordinate, FieldCoordinateBounds.FIELD,
			1, false);
		for (FieldCoordinate adjacentCoordinate : adjacentCoordinates) {
			Player<?> player = fieldModel.getPlayer(adjacentCoordinate);
			if ((player != null) && (player.getTeam() != team) && player.getPosition().getType() != PlayerType.STAR
				&& player.getStrengthWithModifiers() <= MAX_OPPONENT_STRENGTH) {
				opponents.add(player);
			}
		}
		return opponents;
	}
}
