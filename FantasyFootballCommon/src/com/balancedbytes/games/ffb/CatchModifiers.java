package com.balancedbytes.games.ffb;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;

public class CatchModifiers {

	public static final CatchModifier ACCURATE = new CatchModifier("Accurate Pass", -1, false, false);
	public static final CatchModifier NERVES_OF_STEEL = new CatchModifier("Nerves of Steel", 0, false, false);
	public static final CatchModifier EXTRA_ARMS = new CatchModifier("Extra Arms", -1, false, false);
	public static final CatchModifier POURING_RAIN = new CatchModifier("Pouring Rain", 1, false, false);
	public static final CatchModifier TACKLEZONES_1 = new CatchModifier("1 Tacklezone", 1, true, false);
	public static final CatchModifier TACKLEZONES_2 = new CatchModifier("2 Tacklezones", 2, true, false);
	public static final CatchModifier TACKLEZONES_3 = new CatchModifier("3 Tacklezones", 3, true, false);
	public static final CatchModifier TACKLEZONES_4 = new CatchModifier("4 Tacklezones", 4, true, false);
	public static final CatchModifier TACKLEZONES_5 = new CatchModifier("5 Tacklezones", 5, true, false);
	public static final CatchModifier TACKLEZONES_6 = new CatchModifier("6 Tacklezones", 6, true, false);
	public static final CatchModifier TACKLEZONES_7 = new CatchModifier("7 Tacklezones", 7, true, false);
	public static final CatchModifier TACKLEZONES_8 = new CatchModifier("8 Tacklezones", 8, true, false);
	public static final CatchModifier DISTURBING_PRESENCES_1 = new CatchModifier("1 Disturbing Presence", 1, false, true);
	public static final CatchModifier DISTURBING_PRESENCES_2 = new CatchModifier("2 Disturbing Presences", 2, false,
			true);
	public static final CatchModifier DISTURBING_PRESENCES_3 = new CatchModifier("3 Disturbing Presences", 3, false,
			true);
	public static final CatchModifier DISTURBING_PRESENCES_4 = new CatchModifier("4 Disturbing Presences", 4, false,
			true);
	public static final CatchModifier DISTURBING_PRESENCES_5 = new CatchModifier("5 Disturbing Presences", 5, false,
			true);
	public static final CatchModifier DISTURBING_PRESENCES_6 = new CatchModifier("6 Disturbing Presences", 6, false,
			true);
	public static final CatchModifier DISTURBING_PRESENCES_7 = new CatchModifier("7 Disturbing Presences", 7, false,
			true);
	public static final CatchModifier DISTURBING_PRESENCES_8 = new CatchModifier("8 Disturbing Presences", 8, false,
			true);
	public static final CatchModifier DISTURBING_PRESENCES_9 = new CatchModifier("9 Disturbing Presences", 9, false,
			true);
	public static final CatchModifier DISTURBING_PRESENCES_10 = new CatchModifier("10 Disturbing Presences", 10, false,
			true);
	public static final CatchModifier DISTURBING_PRESENCES_11 = new CatchModifier("11 Disturbing Presences", 11, false,
			true);
	public static final CatchModifier HAND_OFF = new CatchModifier("Hand Off", -1, false, false);

	private Map<String, CatchModifier> values;

	public Map<String, CatchModifier> values() {
		return values;
	}

	public static final CatchModifier DIVING_CATCH = new CatchModifier("Diving Catch", -1, false, false) {
		@Override
		public boolean appliesToContext(Skill skill, CatchContext context) {

			if ((CatchScatterThrowInMode.CATCH_ACCURATE_PASS == context.catchMode)
					|| (CatchScatterThrowInMode.CATCH_ACCURATE_BOMB == context.catchMode)) {
				return true;
			}
			return false;

		}
	};

	public CatchModifiers() {
		values = new HashMap<>();
		try {
			Class<?> c = this.getClass();
			Class<?> cModifierType = CatchModifier.class;
			for (Field f : c.getDeclaredFields()) {
				if (f.getType() == cModifierType) {
					CatchModifier modifier = (CatchModifier) f.get(this);
					values.put(modifier.getName().toLowerCase(), modifier);
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class CatchContext {
		public Player<?> player;
		public CatchScatterThrowInMode catchMode;

		public CatchContext(Player<?> pPlayer, CatchScatterThrowInMode pCatchMode) {
			this.player = pPlayer;
			this.catchMode = pCatchMode;
		}
	}
}
