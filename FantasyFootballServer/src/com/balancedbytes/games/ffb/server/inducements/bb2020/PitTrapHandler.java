package com.balancedbytes.games.ffb.server.inducements.bb2020;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.CardHandlerKey;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.inducements.CardHandler;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;

import static com.balancedbytes.games.ffb.inducement.bb2020.CardHandlerKey.PIT_TRAP;

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
