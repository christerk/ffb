package com.fumbbl.ffb.server.inducements.bb2016;

import static com.fumbbl.ffb.inducement.bb2016.CardHandlerKey.RABBITS_FOOT;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardHandlerKey;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.inducements.CardHandler;

@RulesCollection(RulesCollection.Rules.BB2016)
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
