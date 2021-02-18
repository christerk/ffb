package com.balancedbytes.games.ffb.modifiers.bb2020;

import com.balancedbytes.games.ffb.CatchScatterThrowInMode;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.ModifierDictionary;
import com.balancedbytes.games.ffb.modifiers.CatchContext;
import com.balancedbytes.games.ffb.modifiers.CatchModifier;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2020)
public class CatchModifierCollection extends com.balancedbytes.games.ffb.modifiers.CatchModifierCollection {

	public void postConstruct(ModifierDictionary dictionary) {
		super.postConstruct(dictionary);

		add(new CatchModifier("Inaccurate Pass", 1, false, false, dictionary) {
			private final Set<CatchScatterThrowInMode> scatter = new HashSet<CatchScatterThrowInMode>() {{
				add(CatchScatterThrowInMode.CATCH_BOMB);
				add(CatchScatterThrowInMode.CATCH_SCATTER);
			}};
			@Override
			public boolean appliesToContext(CatchContext context) {
				return super.appliesToContext(context) && scatter.contains(context.getCatchMode());
			}
		});

		add(new CatchModifier("Deflected Pass", 1, false, false, dictionary) {
			private final Set<CatchScatterThrowInMode> deflected = new HashSet<CatchScatterThrowInMode>() {{
				add(CatchScatterThrowInMode.DEFLECTED);
				add(CatchScatterThrowInMode.DEFLECTED_BOMB);
			}};
			@Override
			public boolean appliesToContext(CatchContext context) {
				return super.appliesToContext(context) && deflected.contains(context.getCatchMode());
			}
		});
	}
}
