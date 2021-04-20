package com.fumbbl.ffb.server.inducements.bb2016;

import static com.fumbbl.ffb.inducement.bb2016.CardHandlerKey.FORCE_SHIELD;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardHandlerKey;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.inducements.CardHandler;
import com.fumbbl.ffb.util.UtilPlayer;

@RulesCollection(RulesCollection.Rules.BB2016)
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
