package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.modifiers.ModifierType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PickupModifiers {
	public static final PickupModifier BIG_HAND = new PickupModifier("Big Hand", 0, ModifierType.REGULAR);
	public static final PickupModifier POURING_RAIN = new PickupModifier("Pouring Rain", 1, ModifierType.REGULAR);
	public static final PickupModifier EXTRA_ARMS = new PickupModifier("Extra Arms", -1, ModifierType.REGULAR);
	public static final PickupModifier TACKLEZONES_1 = new PickupModifier("1 Tacklezone", 1, ModifierType.TACKLEZONE);
	public static final PickupModifier TACKLEZONES_2 = new PickupModifier("2 Tacklezones", 2, ModifierType.TACKLEZONE);
	public static final PickupModifier TACKLEZONES_3 = new PickupModifier("3 Tacklezones", 3, ModifierType.TACKLEZONE);
	public static final PickupModifier TACKLEZONES_4 = new PickupModifier("4 Tacklezones", 4, ModifierType.TACKLEZONE);
	public static final PickupModifier TACKLEZONES_5 = new PickupModifier("5 Tacklezones", 5, ModifierType.TACKLEZONE);
	public static final PickupModifier TACKLEZONES_6 = new PickupModifier("6 Tacklezones", 6, ModifierType.TACKLEZONE);
	public static final PickupModifier TACKLEZONES_7 = new PickupModifier("7 Tacklezones", 7, ModifierType.TACKLEZONE);
	public static final PickupModifier TACKLEZONES_8 = new PickupModifier("8 Tacklezones", 8, ModifierType.TACKLEZONE);

	private Map<String, PickupModifier> values;

	public Map<String, PickupModifier> values() {
		return values;
	}

	public PickupModifiers() {
		values = new HashMap<>();
		try {
			Class<?> c = this.getClass();
			Class<?> cModifierType = PickupModifier.class;
			for (Field f : c.getDeclaredFields()) {
				if (f.getType() == cModifierType) {
					PickupModifier modifier = (PickupModifier) f.get(this);
					values.put(modifier.getName().toLowerCase(), modifier);
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class PickupContext {
		public PickupContext() {
		}
	}

}
