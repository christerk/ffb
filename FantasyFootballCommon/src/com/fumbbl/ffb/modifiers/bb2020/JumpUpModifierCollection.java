package com.fumbbl.ffb.modifiers.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.modifiers.JumpUpModifier;
import com.fumbbl.ffb.modifiers.ModifierType;

@RulesCollection(RulesCollection.Rules.BB2020)
public class JumpUpModifierCollection extends com.fumbbl.ffb.modifiers.JumpUpModifierCollection {
	public JumpUpModifierCollection() {
		add(new JumpUpModifier("Jump Up", -1, ModifierType.REGULAR));
	}
}
