package com.fumbbl.ffb.modifiers.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.modifiers.GazeModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2020)
public class GazeModifierCollection extends com.fumbbl.ffb.modifiers.GazeModifierCollection {

	public GazeModifierCollection() {
		add(new GazeModifier("1 Tacklezone", "0 for being marked by 1 player (including target)", 0, 1, ModifierType.TACKLEZONE));
		add(new GazeModifier("2 Tacklezones", "1 for being marked by 2 players (including target)", 1, 2, ModifierType.TACKLEZONE));
		add(new GazeModifier("3 Tacklezones", "2 for being marked by 3 players (including target)", 2, 3, ModifierType.TACKLEZONE));
		add(new GazeModifier("4 Tacklezones", "3 for being marked by 4 players (including target)", 3, 4, ModifierType.TACKLEZONE));
		add(new GazeModifier("5 Tacklezones", "4 for being marked by 5 players (including target)", 4, 5, ModifierType.TACKLEZONE));
		add(new GazeModifier("6 Tacklezones", "5 for being marked by 6 players (including target)", 5, 6, ModifierType.TACKLEZONE));
		add(new GazeModifier("7 Tacklezones", "6 for being marked by 7 players (including target)", 6, 7, ModifierType.TACKLEZONE));
		add(new GazeModifier("8 Tacklezones", "7 for being marked by 8 players (including target)", 7, 8, ModifierType.TACKLEZONE));
	}
}
