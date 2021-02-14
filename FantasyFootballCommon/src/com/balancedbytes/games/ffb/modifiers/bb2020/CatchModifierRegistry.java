package com.balancedbytes.games.ffb.modifiers.bb2020;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.modifiers.CatchContext;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;
import com.balancedbytes.games.ffb.modifiers.CatchModifierKey;

@RulesCollection(RulesCollection.Rules.BB2020)
public class CatchModifierRegistry extends com.balancedbytes.games.ffb.modifiers.CatchModifierRegistry {
	public CatchModifierRegistry() {

		add(new CatchModifier(CatchModifierKey.INACCURATE, 1, false, false));
		add(new CatchModifier(CatchModifierKey.DEFLECTED, 1, false, false));
		add(new CatchModifier(CatchModifierKey.NERVES_OF_STEEL, 0, false, false));
		add(new CatchModifier(CatchModifierKey.EXTRA_ARMS, -1, false, false));
		add(new CatchModifier(CatchModifierKey.POURING_RAIN, 1, false, false));
		add(new CatchModifier(CatchModifierKey.TACKLEZONES_1, 1, true, false));
		add(new CatchModifier(CatchModifierKey.TACKLEZONES_2, 2, true, false));
		add(new CatchModifier(CatchModifierKey.TACKLEZONES_3, 3, true, false));
		add(new CatchModifier(CatchModifierKey.TACKLEZONES_4, 4, true, false));
		add(new CatchModifier(CatchModifierKey.TACKLEZONES_5, 5, true, false));
		add(new CatchModifier(CatchModifierKey.TACKLEZONES_6, 6, true, false));
		add(new CatchModifier(CatchModifierKey.TACKLEZONES_7, 7, true, false));
		add(new CatchModifier(CatchModifierKey.TACKLEZONES_8, 8, true, false));
		add(new CatchModifier(CatchModifierKey.DISTURBING_PRESENCES_1, 1, false, true));
		add(new CatchModifier(CatchModifierKey.DISTURBING_PRESENCES_2, 2, false,
			true));
		add(new CatchModifier(CatchModifierKey.DISTURBING_PRESENCES_3, 3, false,
			true));
		add(new CatchModifier(CatchModifierKey.DISTURBING_PRESENCES_4, 4, false,
			true));
		add(new CatchModifier(CatchModifierKey.DISTURBING_PRESENCES_5, 5, false,
			true));
		add(new CatchModifier(CatchModifierKey.DISTURBING_PRESENCES_6, 6, false,
			true));
		add(new CatchModifier(CatchModifierKey.DISTURBING_PRESENCES_7, 7, false,
			true));
		add(new CatchModifier(CatchModifierKey.DISTURBING_PRESENCES_8, 8, false,
			true));
		add(new CatchModifier(CatchModifierKey.DISTURBING_PRESENCES_9, 9, false,
			true));
		add(new CatchModifier(CatchModifierKey.DISTURBING_PRESENCES_10, 10, false,
			true));
		add(new CatchModifier(CatchModifierKey.DISTURBING_PRESENCES_11, 11, false,
			true));

		add(new CatchModifier(CatchModifierKey.DIVING_CATCH, -1, false, false) {
			@Override
			public boolean appliesToContext(Skill skill, CatchContext context) {

				return (CatchScatterThrowInMode.CATCH_ACCURATE_PASS == context.catchMode)
					|| (CatchScatterThrowInMode.CATCH_ACCURATE_BOMB == context.catchMode);

			}
		});
	}
}
