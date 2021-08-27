package com.fumbbl.ffb.modifiers.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.modifiers.GazeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2016)
public class GazeModifierCollection extends com.fumbbl.ffb.modifiers.GazeModifierCollection {

	public GazeModifierCollection() {
		add(new GazeModifier("1 Tacklezone", 1, ModifierType.TACKLEZONE));
		add(new GazeModifier("2 Tacklezones", 2, ModifierType.TACKLEZONE));
		add(new GazeModifier("3 Tacklezones", 3, ModifierType.TACKLEZONE));
		add(new GazeModifier("4 Tacklezones", 4, ModifierType.TACKLEZONE));
		add(new GazeModifier("5 Tacklezones", 5, ModifierType.TACKLEZONE));
		add(new GazeModifier("6 Tacklezones", 6, ModifierType.TACKLEZONE));
		add(new GazeModifier("7 Tacklezones", 7, ModifierType.TACKLEZONE));
		add(new GazeModifier("8 Tacklezones", 8, ModifierType.TACKLEZONE));
	}
}
