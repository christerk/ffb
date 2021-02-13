package com.balancedbytes.games.ffb.modifiers;

import java.util.HashMap;

import static com.balancedbytes.games.ffb.modifiers.InterceptionModifierKey.*;

public class InterceptionModifierRegistry extends HashMap<InterceptionModifierKey, InterceptionModifier> implements ModifierKey {
	{
		add(new InterceptionModifier(NERVES_OF_STEEL, 0, false, false));
		add(new InterceptionModifier(EXTRA_ARMS, -1, false, false));
		add(new InterceptionModifier(VERY_LONG_LEGS, -1, false,
			false));
		add(new InterceptionModifier(POURING_RAIN, 1, false, false));
		add(new InterceptionModifier(TACKLEZONES_1, 1, true, false));
		add(new InterceptionModifier(TACKLEZONES_2, 2, true, false));
		add(new InterceptionModifier(TACKLEZONES_3, 3, true, false));
		add(new InterceptionModifier(TACKLEZONES_4, 4, true, false));
		add(new InterceptionModifier(TACKLEZONES_5, 5, true, false));
		add(new InterceptionModifier(TACKLEZONES_6, 6, true, false));
		add(new InterceptionModifier(TACKLEZONES_7, 7, true, false));
		add(new InterceptionModifier(TACKLEZONES_8, 8, true, false));
		add(new InterceptionModifier(DISTURBING_PRESENCES_1, 1,
			false, true));
		add(new InterceptionModifier(DISTURBING_PRESENCES_2,
			2, false, true));
		add(new InterceptionModifier(DISTURBING_PRESENCES_3,
			3, false, true));
		add(new InterceptionModifier(DISTURBING_PRESENCES_4,
			4, false, true));
		add(new InterceptionModifier(DISTURBING_PRESENCES_5,
			5, false, true));
		add(new InterceptionModifier(DISTURBING_PRESENCES_6,
			6, false, true));
		add(new InterceptionModifier(DISTURBING_PRESENCES_7,
			7, false, true));
		add(new InterceptionModifier(DISTURBING_PRESENCES_8,
			8, false, true));
		add(new InterceptionModifier(DISTURBING_PRESENCES_9,
			9, false, true));
		add(new InterceptionModifier(DISTURBING_PRESENCES_10,
			10, false, true));
		add(new InterceptionModifier(DISTURBING_PRESENCES_11,
			11, false, true));
		add(new InterceptionModifier(FAWNDOUGHS_HEADBAND, -1,
			false, false));
		add(new InterceptionModifier(
			MAGIC_GLOVES_OF_JARK_LONGARM, -1, false, false));
	}

	private void add(InterceptionModifier modifier) {
		put(modifier.getModifierKey(), modifier);
	}
}
