package com.balancedbytes.games.ffb;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PassingModifiers {
	public static Set<PassModifier> tackleZoneModifiers = new HashSet<PassModifier>();
	public static Set<PassModifier> disturbingPresenceModifiers = new HashSet<PassModifier>();

	public static final PassModifier VERY_SUNNY = new PassModifier("Very Sunny", 1, false, false);
	public static final PassModifier BLIZZARD = new PassModifier("Blizzard", 0, false, false);
	public static final PassModifier TACKLEZONES_1 = new PassModifier("1 Tacklezone", 1, true, false);
	public static final PassModifier TACKLEZONES_2 = new PassModifier("2 Tacklezones", 2, true, false);
	public static final PassModifier TACKLEZONES_3 = new PassModifier("3 Tacklezones", 3, true, false);
	public static final PassModifier TACKLEZONES_4 = new PassModifier("4 Tacklezones", 4, true, false);
	public static final PassModifier TACKLEZONES_5 = new PassModifier("5 Tacklezones", 5, true, false);
	public static final PassModifier TACKLEZONES_6 = new PassModifier("6 Tacklezones", 6, true, false);
	public static final PassModifier TACKLEZONES_7 = new PassModifier("7 Tacklezones", 7, true, false);
	public static final PassModifier TACKLEZONES_8 = new PassModifier("8 Tacklezones", 8, true, false);
	public static final PassModifier DISTURBING_PRESENCES_1 = new PassModifier("1 Disturbing Presence", 1, false, true);
	public static final PassModifier DISTURBING_PRESENCES_2 = new PassModifier("2 Disturbing Presences", 2, false, true);
	public static final PassModifier DISTURBING_PRESENCES_3 = new PassModifier("3 Disturbing Presences", 3, false, true);
	public static final PassModifier DISTURBING_PRESENCES_4 = new PassModifier("4 Disturbing Presences", 4, false, true);
	public static final PassModifier DISTURBING_PRESENCES_5 = new PassModifier("5 Disturbing Presences", 5, false, true);
	public static final PassModifier DISTURBING_PRESENCES_6 = new PassModifier("6 Disturbing Presences", 6, false, true);
	public static final PassModifier DISTURBING_PRESENCES_7 = new PassModifier("7 Disturbing Presences", 7, false, true);
	public static final PassModifier DISTURBING_PRESENCES_8 = new PassModifier("8 Disturbing Presences", 8, false, true);
	public static final PassModifier DISTURBING_PRESENCES_9 = new PassModifier("9 Disturbing Presences", 9, false, true);
	public static final PassModifier DISTURBING_PRESENCES_10 = new PassModifier("10 Disturbing Presences", 10, false,
			true);
	public static final PassModifier DISTURBING_PRESENCES_11 = new PassModifier("11 Disturbing Presences", 11, false,
			true);
	public static final PassModifier GROMSKULLS_EXPLODING_RUNES = new PassModifier("Gromskull's Exploding Runes", 1,
			false, false);

	private Map<String, PassModifier> values;

	public Map<String, PassModifier> values() {
		return values;
	}

	public PassingModifiers() {
		values = new HashMap<String, PassModifier>();
		try {
			Class<?> c = this.getClass();
			Class<?> cModifierType = PassModifier.class;
			for (Field f : c.getDeclaredFields()) {
				if (f.getType() == cModifierType) {
					PassModifier modifier = (PassModifier) f.get(this);
					values.put(modifier.getName().toLowerCase(), modifier);
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class PassContext {
		public PassingDistance distance;
		public boolean duringThrowTeamMate;

		public PassContext(PassingDistance distance, boolean duringThrowTeamMate) {
			this.distance = distance;
			this.duringThrowTeamMate = duringThrowTeamMate;
		}
	}

}
