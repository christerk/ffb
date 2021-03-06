package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

public class PickupModifierCollection extends ModifierCollection<PickupContext, PickupModifier> {
	
	public PickupModifierCollection() {
		add(new PickupModifier("Pouring Rain", 1, ModifierType.REGULAR) {
			@Override
			public boolean appliesToContext(Skill skill, PickupContext context) {
				return context.getGame().getFieldModel().getWeather() == Weather.POURING_RAIN
					&& !context.getPlayer().hasSkillWithProperty(NamedProperties.ignoreWeatherWhenPickingUp);
			}
		});
		add(new PickupModifier("1 Tacklezone", 1, ModifierType.TACKLEZONE));
		add(new PickupModifier("2 Tacklezones", 2, ModifierType.TACKLEZONE));
		add(new PickupModifier("3 Tacklezones", 3, ModifierType.TACKLEZONE));
		add(new PickupModifier("4 Tacklezones", 4, ModifierType.TACKLEZONE));
		add(new PickupModifier("5 Tacklezones", 5, ModifierType.TACKLEZONE));
		add(new PickupModifier("6 Tacklezones", 6, ModifierType.TACKLEZONE));
		add(new PickupModifier("7 Tacklezones", 7, ModifierType.TACKLEZONE));
		add(new PickupModifier("8 Tacklezones", 8, ModifierType.TACKLEZONE));

	}
}
