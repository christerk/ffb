package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.ArrayList;
import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2016)
public class TtmMechanic extends com.fumbbl.ffb.mechanics.TtmMechanic {

	public Player<?>[] findThrowableTeamMates(Game pGame, Player<?> pThrower) {
		List<Player<?>> throwablePlayers = new ArrayList<>();
		FieldModel fieldModel = pGame.getFieldModel();
		FieldCoordinate throwerCoordinate = fieldModel.getPlayerCoordinate(pThrower);
		Player<?>[] adjacentPlayers = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, pThrower.getTeam(), throwerCoordinate,
			false);
		for (Player<?> adjacentPlayer : adjacentPlayers) {

			if (adjacentPlayer.canBeThrown()) {
				throwablePlayers.add(adjacentPlayer);
			}
		}
		return throwablePlayers.toArray(new Player[0]);
	}

	@Override
	public boolean canBeThrown(Game game, Player<?> player) {
		return player.canBeThrown()
			&& game.getFieldModel().getPlayerState(player).hasTacklezones()
			&& game.getActingTeam() == player.getTeam();
	}
}
