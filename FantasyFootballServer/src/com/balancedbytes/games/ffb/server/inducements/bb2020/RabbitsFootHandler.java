package com.balancedbytes.games.ffb.server.inducements.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.CardHandlerKey;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.server.inducements.CardHandler;

import static com.balancedbytes.games.ffb.inducement.bb2020.CardHandlerKey.RABBITS_FOOT;

@RulesCollection(RulesCollection.Rules.BB2020)
public class RabbitsFootHandler extends CardHandler {
	@Override
	protected CardHandlerKey handlerKey() {
		return RABBITS_FOOT;
	}

	@Override
	public boolean allowsPlayer(Game game, Card card, Player<?> player) {
		return !player.hasSkillProperty(NamedProperties.preventCardRabbitsFoot);
	}
}
