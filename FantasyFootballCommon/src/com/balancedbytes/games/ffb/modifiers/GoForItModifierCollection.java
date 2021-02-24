package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.util.UtilCards;

public class GoForItModifierCollection extends ModifierCollection<GoForItContext, GoForItModifier> {
	public GoForItModifierCollection() {
		add(new GoForItModifier("Blizzard", 1));
		add(new GoForItModifier("Greased Shoes", 3) {
			@Override
			public boolean appliesToContext(Skill skill, GoForItContext context) {
				return UtilCards.isCardActive(context.getGame(), Card.GREASED_SHOES) && context.getGame().getFieldModel().getWeather() != Weather.BLIZZARD;
			}
		});
		add(new GoForItModifier("Greased Shoes in Blizzard", 2){
			@Override
			public boolean appliesToContext(Skill skill, GoForItContext context) {
				return UtilCards.isCardActive(context.getGame(), Card.GREASED_SHOES) && context.getGame().getFieldModel().getWeather() == Weather.BLIZZARD;
			}
		});

	}
}
