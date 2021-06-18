package com.fumbbl.ffb.server.inducements.bb2016.cards;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardHandlerKey;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.inducements.CardHandler;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.util.UtilPlayer;

import static com.fumbbl.ffb.inducement.bb2016.CardHandlerKey.CUSTARD_PIE;

@RulesCollection(RulesCollection.Rules.BB2016)
public class CustardPieHandler extends CardHandler {
	@Override
	protected CardHandlerKey handlerKey() {
		return CUSTARD_PIE;
	}

	@Override
	public boolean activate(Card card, IStep step, Player<?> player) {
		Game game = step.getGameState().getGame();
		PlayerState playerState = game.getFieldModel().getPlayerState(player);
		game.getFieldModel().setPlayerState(player, playerState.changeHypnotized(true));
		return true;
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
