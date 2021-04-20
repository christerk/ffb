package com.fumbbl.ffb.server.inducements.bb2020;

import static com.fumbbl.ffb.inducement.bb2020.CardHandlerKey.PIT_TRAP;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardHandlerKey;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.inducements.CardHandler;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.util.UtilServerInjury;

@RulesCollection(RulesCollection.Rules.BB2020)
public class PitTrapHandler extends CardHandler {
	@Override
	protected CardHandlerKey handlerKey() {
		return PIT_TRAP;
	}

	@Override
	public boolean activate(Card card, IStep step, Player<?> player) {
		step.publishParameters(UtilServerInjury.dropPlayer(step, player, ApothecaryMode.DEFENDER));
		return true;
	}
}
