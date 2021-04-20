package com.fumbbl.ffb.server.inducements.bb2016;

import static com.fumbbl.ffb.inducement.bb2016.CardHandlerKey.ILLEGAL_SUBSTITUTION;

import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardHandlerKey;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.inducements.CardHandler;
import com.fumbbl.ffb.server.step.IStep;

@RulesCollection(RulesCollection.Rules.BB2016)
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
