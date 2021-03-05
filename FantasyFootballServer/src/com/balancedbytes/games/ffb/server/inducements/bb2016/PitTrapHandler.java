package com.balancedbytes.games.ffb.server.inducements.bb2016;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.CardHandlerKey;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.inducements.CardHandler;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.util.UtilServerInjury;

import static com.balancedbytes.games.ffb.inducement.bb2016.CardHandlerKey.PIT_TRAP;

@RulesCollection(RulesCollection.Rules.BB2016)
public class PitTrapHandler extends CardHandler {
	@Override
	protected CardHandlerKey handlerKey() {
		return PIT_TRAP;
	}

	@Override
	public void activate(Card card, IStep step, Player<?> player) {
		step.publishParameters(UtilServerInjury.dropPlayer(step, player, ApothecaryMode.DEFENDER));
	}
}
