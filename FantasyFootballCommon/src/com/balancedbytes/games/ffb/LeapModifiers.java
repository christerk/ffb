package com.balancedbytes.games.ffb;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.modifiers.ModifierType;

public class LeapModifiers {

	public static final LeapModifier VERY_LONG_LEGS = new LeapModifier("Very Long Legs", -1, ModifierType.REGULAR);

	private Map<String, LeapModifier> values;

	public Map<String, LeapModifier> values() {
		return values;
	}

	public LeapModifiers() {
		values = new HashMap<>();
		try {
			Class<?> c = this.getClass();
			Class<?> cModifierType = LeapModifier.class;
			for (Field f : c.getDeclaredFields()) {
				if (f.getType() == cModifierType) {
					LeapModifier modifier = (LeapModifier) f.get(this);
					values.put(modifier.getName().toLowerCase(), modifier);
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class LeapContext {
		public ActingPlayer actingPlayer;
		public FieldCoordinate sourceCoordinate;

		public LeapContext(ActingPlayer actingPlayer, FieldCoordinate sourceCoordinate) {
			this.sourceCoordinate = sourceCoordinate;
			this.actingPlayer = actingPlayer;
		}
	}
}
