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
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import static com.balancedbytes.games.ffb.inducement.bb2016.CardHandlerKey.CUSTARD_PIE;

@RulesCollection(RulesCollection.Rules.BB2016)
public class CustardPieHandler extends CardHandler {
	@Override
	protected CardHandlerKey handlerKey() {
		return CUSTARD_PIE;
	}

	@Override
	public void activate(Card card, IStep step, Player<?> player) {
		Game game = step.getGameState().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		game.getFieldModel().setPlayerState(player, playerState.changeHypnotized(true));
	}

	@Override
	public void deactivate(Card card, IStep step, Player<?> player) {
		Game game = step.getGameState().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		if ((playerState != null) && playerState.isHypnotized()) {
			game.getFieldModel().setPlayerState(player, playerState.changeHypnotized(false));
		}
	}

	@Override
	public boolean allowsPlayer(Game game, Card card, Player<?> player) {
		Team ownTeam = game.getTurnDataHome().getInducementSet().isAvailable(card) ? game.getTeamHome()
			: game.getTeamAway();
		FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);

		return UtilPlayer.findAdjacentStandingOrPronePlayers(game, ownTeam, playerCoordinate).length > 0;
	}
}
