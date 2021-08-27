package com.fumbbl.ffb.server.inducements.bb2020.cards;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardHandlerKey;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.inducements.CardHandler;
import com.fumbbl.ffb.util.UtilPlayer;

import static com.fumbbl.ffb.inducement.bb2020.CardHandlerKey.FORCE_SHIELD;

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
