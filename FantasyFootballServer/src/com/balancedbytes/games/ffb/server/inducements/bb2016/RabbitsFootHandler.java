package com.balancedbytes.games.ffb.server.inducements.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.inducement.Card;
import com.balancedbytes.games.ffb.inducement.CardHandlerKey;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.server.inducements.CardHandler;

import static com.balancedbytes.games.ffb.inducement.bb2016.CardHandlerKey.RABBITS_FOOT;

@RulesCollection(RulesCollection.Rules.BB2016)
public class RabbitsFootHandler extends CardHandler {
	@Override
	protected CardHandlerKey handlerKey() {
		return RABBITS_FOOT;
	}

	@Override
	public boolean allowsPlayer(Game game, Card card, Player<?> player) {
		return !player.hasSkillWithProperty(NamedProperties.preventCardRabbitsFoot);
	}
}
