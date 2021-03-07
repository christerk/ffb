package com.balancedbytes.games.ffb.server.inducements.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.CardHandlerKey;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.server.inducements.CardHandler;
import com.balancedbytes.games.ffb.util.UtilPlayer;

import static com.balancedbytes.games.ffb.inducement.bb2020.CardHandlerKey.FORCE_SHIELD;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ForceShieldHandler extends CardHandler {
	@Override
	protected CardHandlerKey handlerKey() {
		return FORCE_SHIELD;
	}

	@Override
	public boolean allowsPlayer(Game game, Card card, Player<?> player) {
		return UtilPlayer.hasBall(game, player);
	}
}
