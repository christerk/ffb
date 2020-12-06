package com.balancedbytes.games.ffb;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.model.Player;

public class InterceptionModifiers {

	public static final InterceptionModifier NERVES_OF_STEEL = new InterceptionModifier("Nerves of Steel", 0, false,
			false);
	public static final InterceptionModifier EXTRA_ARMS = new InterceptionModifier("Extra Arms", -1, false, false);
	public static final InterceptionModifier VERY_LONG_LEGS = new InterceptionModifier("Very Long Legs", -1, false,
			false);
	public static final InterceptionModifier POURING_RAIN = new InterceptionModifier("Pouring Rain", 1, false, false);
	public static final InterceptionModifier TACKLEZONES_1 = new InterceptionModifier("1 Tacklezone", 1, true, false);
	public static final InterceptionModifier TACKLEZONES_2 = new InterceptionModifier("2 Tacklezones", 2, true, false);
	public static final InterceptionModifier TACKLEZONES_3 = new InterceptionModifier("3 Tacklezones", 3, true, false);
	public static final InterceptionModifier TACKLEZONES_4 = new InterceptionModifier("4 Tacklezones", 4, true, false);
	public static final InterceptionModifier TACKLEZONES_5 = new InterceptionModifier("5 Tacklezones", 5, true, false);
	public static final InterceptionModifier TACKLEZONES_6 = new InterceptionModifier("6 Tacklezones", 6, true, false);
	public static final InterceptionModifier TACKLEZONES_7 = new InterceptionModifier("7 Tacklezones", 7, true, false);
	public static final InterceptionModifier TACKLEZONES_8 = new InterceptionModifier("8 Tacklezones", 8, true, false);
	public static final InterceptionModifier DISTURBING_PRESENCES_1 = new InterceptionModifier("1 Disturbing Presence", 1,
			false, true);
	public static final InterceptionModifier DISTURBING_PRESENCES_2 = new InterceptionModifier("2 Disturbing Presences",
			2, false, true);
	public static final InterceptionModifier DISTURBING_PRESENCES_3 = new InterceptionModifier("3 Disturbing Presences",
			3, false, true);
	public static final InterceptionModifier DISTURBING_PRESENCES_4 = new InterceptionModifier("4 Disturbing Presences",
			4, false, true);
	public static final InterceptionModifier DISTURBING_PRESENCES_5 = new InterceptionModifier("5 Disturbing Presences",
			5, false, true);
	public static final InterceptionModifier DISTURBING_PRESENCES_6 = new InterceptionModifier("6 Disturbing Presences",
			6, false, true);
	public static final InterceptionModifier DISTURBING_PRESENCES_7 = new InterceptionModifier("7 Disturbing Presences",
			7, false, true);
	public static final InterceptionModifier DISTURBING_PRESENCES_8 = new InterceptionModifier("8 Disturbing Presences",
			8, false, true);
	public static final InterceptionModifier DISTURBING_PRESENCES_9 = new InterceptionModifier("9 Disturbing Presences",
			9, false, true);
	public static final InterceptionModifier DISTURBING_PRESENCES_10 = new InterceptionModifier("10 Disturbing Presences",
			10, false, true);
	public static final InterceptionModifier DISTURBING_PRESENCES_11 = new InterceptionModifier("11 Disturbing Presences",
			11, false, true);
	public static final InterceptionModifier FAWNDOUGHS_HEADBAND = new InterceptionModifier("Fawndough's Headband", -1,
			false, false);
	public static final InterceptionModifier MAGIC_GLOVES_OF_JARK_LONGARM = new InterceptionModifier(
			"Magic Gloves of Jark Longarm", -1, false, false);

	private Map<String, InterceptionModifier> values;

	public Map<String, InterceptionModifier> values() {
		return values;
	}

	public InterceptionModifiers() {
		values = new HashMap<String, InterceptionModifier>();
		try {
			Class<?> c = this.getClass();
			Class<?> cModifierType = InterceptionModifier.class;
			for (Field f : c.getDeclaredFields()) {
				if (f.getType() == cModifierType) {
					InterceptionModifier modifier = (InterceptionModifier) f.get(this);
					values.put(modifier.getName().toLowerCase(), modifier);
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class InterceptionContext {
		public Player<?> player;

		public InterceptionContext(Player<?> player) {
			this.player = player;
		}
	}
}
