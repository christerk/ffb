package com.balancedbytes.games.ffb.modifiers.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifier;
import com.balancedbytes.games.ffb.modifiers.InterceptionModifierKey;

import java.util.HashMap;

import static com.balancedbytes.games.ffb.modifiers.InterceptionModifierKey.*;

@RulesCollection(RulesCollection.Rules.BB2020)
public class InterceptionModifierRegistry extends HashMap<InterceptionModifierKey, InterceptionModifier>
	implements com.balancedbytes.games.ffb.modifiers.InterceptionModifierRegistry {
	{
		add(new InterceptionModifier(NERVES_OF_STEEL, 0, false, false));
		add(new InterceptionModifier(EXTRA_ARMS, -1, false, false));
		add(new InterceptionModifier(VERY_LONG_LEGS, -1, false,
			false));
		add(new InterceptionModifier(POURING_RAIN, 1, false, false));
		add(new InterceptionModifier(TACKLEZONES_1, "1 for 1 or more Tacklezones", 1, 1, true, false));
		add(new InterceptionModifier(TACKLEZONES_2, "1 for 1 or more Tacklezones", 1, 2, true, false));
		add(new InterceptionModifier(TACKLEZONES_3, "1 for 1 or more Tacklezones", 1, 3, true, false));
		add(new InterceptionModifier(TACKLEZONES_4, "1 for 1 or more Tacklezones", 1, 4, true, false));
		add(new InterceptionModifier(TACKLEZONES_5, "1 for 1 or more Tacklezones", 1, 5, true, false));
		add(new InterceptionModifier(TACKLEZONES_6, "1 for 1 or more Tacklezones", 1, 6, true, false));
		add(new InterceptionModifier(TACKLEZONES_7, "1 for 1 or more Tacklezones", 1, 7, true, false));
		add(new InterceptionModifier(TACKLEZONES_8, "1 for 1 or more Tacklezones", 1, 8, true, false));
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
		add(new InterceptionModifier(PASS_ACCURATE, 3, false, false));
		add(new InterceptionModifier(PASS_INACCURATE, 2, false, false));
		add(new InterceptionModifier(PASS_WILDLY_INACCURATE, 1, false, false));
	}

	private void add(InterceptionModifier modifier) {
		put(modifier.getModifierKey(), modifier);
	}
}
