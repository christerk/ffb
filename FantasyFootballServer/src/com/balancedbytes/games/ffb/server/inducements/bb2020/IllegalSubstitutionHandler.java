package com.balancedbytes.games.ffb.server.inducements.bb2020;

import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.TurnMode;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.CardHandlerKey;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.inducements.CardHandler;
import com.balancedbytes.games.ffb.server.step.IStep;

import static com.balancedbytes.games.ffb.inducement.bb2020.CardHandlerKey.ILLEGAL_SUBSTITUTION;

@RulesCollection(RulesCollection.Rules.BB2020)
public class IllegalSubstitutionHandler extends CardHandler {
	@Override
	protected CardHandlerKey handlerKey() {
		return ILLEGAL_SUBSTITUTION;
	}

	@Override
	public boolean activate(Card card, IStep step, Player<?> player) {
		Game game = step.getGameState().getGame();
		game.setTurnMode(TurnMode.ILLEGAL_SUBSTITUTION);
		return false;
	}

	@Override
	public void deactivate(Card card, IStep step, Player<?> unused) {
		Game game = step.getGameState().getGame();
		Player<?>[] players = game.getFieldModel().findPlayers(CardEffect.ILLEGALLY_SUBSTITUTED);
		for (Player<?> player : players) {
			game.getFieldModel().removeCardEffect(player, CardEffect.ILLEGALLY_SUBSTITUTED);
		}
	}
}
