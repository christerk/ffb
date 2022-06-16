package com.fumbbl.ffb.server.inducements.bb2020.cards;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardHandlerKey;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.inducements.CardHandler;
import com.fumbbl.ffb.util.UtilPlayer;

import static com.fumbbl.ffb.inducement.bb2020.CardHandlerKey.CHOP_BLOCK;

@RulesCollection(RulesCollection.Rules.BB2020)
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

		return playerState.isActive() && !playerState.isProneOrStunned()
			&& (UtilPlayer.findAdjacentBlockablePlayers(game, otherTeam, playerCoordinate).length > 0);
	}
}
