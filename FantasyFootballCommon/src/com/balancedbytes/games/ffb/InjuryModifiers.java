package com.balancedbytes.games.ffb;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.util.UtilCards;

public class InjuryModifiers {

	public static final InjuryModifier THICK_SKULL = new InjuryModifier("Thick Skull", 0, false);
	public static final InjuryModifier NIGGLING_INJURIES_1 = new InjuryModifier("1 Niggling Injury", 1, true);
	public static final InjuryModifier NIGGLING_INJURIES_2 = new InjuryModifier("2 Niggling Injuries", 2, true);
	public static final InjuryModifier NIGGLING_INJURIES_3 = new InjuryModifier("3 Niggling Injuries", 3, true);
	public static final InjuryModifier NIGGLING_INJURIES_4 = new InjuryModifier("4 Niggling Injuries", 4, true);
	public static final InjuryModifier NIGGLING_INJURIES_5 = new InjuryModifier("5 Niggling Injuries", 5, true);

	public static final InjuryModifier DIRTY_PLAYER = new InjuryModifier("Dirty Player", 1, false) {
		@Override
		public boolean appliesToContext(InjuryModifierContext context) {
			return (context.isFoul && !context.injuryContext.hasArmorModifier(ArmorModifiers.DIRTY_PLAYER));
		}
	};

	public static final InjuryModifier MIGHTY_BLOW = new InjuryModifier("Mighty Blow", 1, false) {
		@Override
		public boolean appliesToContext(InjuryModifierContext context) {
			return (!context.isFoul && !context.injuryContext.hasArmorModifier(ArmorModifiers.MIGHTY_BLOW));
		}
	};

	public static final InjuryModifier STUNTY = new InjuryModifier("Stunty", 0, false) {
		@Override
		public boolean appliesToContext(InjuryModifierContext context) {
			boolean applies = false;

			if (!context.isStab && 
					!UtilCards.hasCard(context.game, context.defender, Card.GOOD_OLD_MAGIC_CODPIECE) &&
					context.defender.hasSkill(SkillConstants.STUNTY)) {
				applies = true;
			}

			return applies;
		}
	};

	private Map<String, InjuryModifier> values;

	public Map<String, InjuryModifier> values() {
		return values;
	}

	public InjuryModifiers() {
		values = new HashMap<String, InjuryModifier>();
		try {
			Class<?> c = this.getClass();
			Class<?> cModifierType = InjuryModifier.class;
			for (Field f : c.getDeclaredFields()) {
				if (f.getType() == cModifierType) {
					InjuryModifier modifier = (InjuryModifier) f.get(this);
					values.put(modifier.getName().toLowerCase(), modifier);
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
