package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;
import com.fumbbl.ffb.modifiers.StaticInjuryModifier;
import com.fumbbl.ffb.modifiers.StaticInjuryModifierAttacker;
import com.fumbbl.ffb.server.GameState;

import java.util.Collections;

public class AddBlockArmourModifier extends InjuryContextModification<AddBlockArmourModifierParams> {

	private final StaticArmourModifier armourModifier = new StaticArmourModifier("Crushing Blow", 1, false);
	private final StaticInjuryModifier injuryModifier = new StaticInjuryModifierAttacker("Crushing Blow", 1, false);

	public AddBlockArmourModifier() {
		super(Collections.singleton(Block.class));
	}

	@Override
	protected AddBlockArmourModifierParams params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		return new AddBlockArmourModifierParams(gameState, newContext, injuryType);
	}

	@Override
	protected void applyArmourModification(AddBlockArmourModifierParams params) {
		params.getNewContext().addArmorModifier(armourModifier);
	}

	@Override
	SkillUse skillUse() {
		return SkillUse.ADD_ARMOUR_MODIFIER;
	}
}
