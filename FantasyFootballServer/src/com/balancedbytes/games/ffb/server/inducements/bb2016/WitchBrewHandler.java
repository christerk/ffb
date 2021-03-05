package com.balancedbytes.games.ffb.server.inducements.bb2016;

import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.CardHandlerKey;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportCardEffectRoll;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.inducements.CardHandler;
import com.balancedbytes.games.ffb.server.step.IStep;

import static com.balancedbytes.games.ffb.inducement.bb2016.CardHandlerKey.WITCH_BREW;

@RulesCollection(RulesCollection.Rules.BB2016)
public class WitchBrewHandler extends CardHandler {

	@Override
	protected CardHandlerKey handlerKey() {
		return WITCH_BREW;
	}

	@Override
	public void activate(Card card, IStep step, Player<?> player) {
		Game game = step.getGameState().getGame();
		int roll = step.getGameState().getDiceRoller().rollCardEffect();
		CardEffect cardEffect = DiceInterpreter.getInstance().interpretWitchBrewRoll(roll);
		game.getFieldModel().addCardEffect(player, cardEffect);

		ReportCardEffectRoll cardEffectReport = new ReportCardEffectRoll(card, roll);
		cardEffectReport.setCardEffect(cardEffect);
		step.getResult().addReport(cardEffectReport);
	}

	@Override
	public void deactivate(Card card, IStep step, Player<?> player) {
		Game game = step.getGameState().getGame();
		if (game.getFieldModel().hasCardEffect(player, CardEffect.SEDATIVE)) {
			game.getFieldModel().removeCardEffect(player, CardEffect.SEDATIVE);
		}
		if (game.getFieldModel().hasCardEffect(player, CardEffect.MAD_CAP_MUSHROOM_POTION)) {
			game.getFieldModel().removeCardEffect(player, CardEffect.MAD_CAP_MUSHROOM_POTION);
		}	}
}
