package com.balancedbytes.games.ffb.server.inducements.bb2016;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.CardHandlerKey;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.server.inducements.CardHandler;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import static com.balancedbytes.games.ffb.inducement.bb2016.CardHandlerKey.CHOP_BLOCK;

@RulesCollection(RulesCollection.Rules.BB2016)
public class ChopBlockHandler extends CardHandler {
	@Override
	protected CardHandlerKey handlerKey() {
		return CHOP_BLOCK;
	}

	@Override
	public boolean allowsPlayer(Game game, Card card, Player<?> player) {
		Team ownTeam = game.getTurnDataHome().getInducementSet().isAvailable(card) ? game.getTeamHome()
			: game.getTeamAway();
		Team otherTeam = (game.getTeamHome() == ownTeam) ? game.getTeamAway() : game.getTeamHome();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
		PlayerState playerState = game.getFieldModel().getPlayerState(player);

		return playerState.isActive() && !playerState.isProne()
			&& (UtilPlayer.findAdjacentBlockablePlayers(game, otherTeam, playerCoordinate).length > 0);
	}
}
