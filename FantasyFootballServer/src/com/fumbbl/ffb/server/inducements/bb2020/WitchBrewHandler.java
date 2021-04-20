package com.fumbbl.ffb.server.inducements.bb2020;

import static com.fumbbl.ffb.inducement.bb2020.CardHandlerKey.WITCH_BREW;

import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardHandlerKey;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportCardEffectRoll;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.inducements.CardHandler;
import com.fumbbl.ffb.server.step.IStep;

@RulesCollection(RulesCollection.Rules.BB2020)
public class WitchBrewHandler extends CardHandler {

	@Override
	protected CardHandlerKey handlerKey() {
		return WITCH_BREW;
	}

	@Override
	public boolean activate(Card card, IStep step, Player<?> player) {
		Game game = step.getGameState().getGame();
		int roll = step.getGameState().getDiceRoller().rollCardEffect();
		CardEffect cardEffect = DiceInterpreter.getInstance().interpretWitchBrewRoll(roll);
		game.getFieldModel().addCardEffect(player, cardEffect);

		ReportCardEffectRoll cardEffectReport = new ReportCardEffectRoll(card, roll);
		cardEffectReport.setCardEffect(cardEffect);
		step.getResult().addReport(cardEffectReport);
		return true;
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
