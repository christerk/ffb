package com.fumbbl.ffb.modifiers.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.modifiers.GazeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2016)
public class GazeModifierCollection extends com.fumbbl.ffb.modifiers.GazeModifierCollection {

	public GazeModifierCollection() {
		add(new GazeModifier("1 Tacklezone", "0 for being in 1 tacklezone (including target)", 0, 1, ModifierType.TACKLEZONE));
		add(new GazeModifier("2 Tacklezones", "1 for being in 2 tacklezones (including target)", 1, 2, ModifierType.TACKLEZONE));
		add(new GazeModifier("3 Tacklezones", "2 for being in 3 tacklezones (including target)", 2, 3, ModifierType.TACKLEZONE));
		add(new GazeModifier("4 Tacklezones", "3 for being in 4 tacklezones (including target)", 3, 4, ModifierType.TACKLEZONE));
		add(new GazeModifier("5 Tacklezones", "4 for being in 5 tacklezones (including target)", 4, 5, ModifierType.TACKLEZONE));
		add(new GazeModifier("6 Tacklezones", "5 for being in 6 tacklezones (including target)", 5, 6, ModifierType.TACKLEZONE));
		add(new GazeModifier("7 Tacklezones", "6 for being in 7 tacklezones (including target)", 6, 7, ModifierType.TACKLEZONE));
		add(new GazeModifier("8 Tacklezones", "7 for being in 8 tacklezones (including target)", 7, 8, ModifierType.TACKLEZONE));
	}
}
