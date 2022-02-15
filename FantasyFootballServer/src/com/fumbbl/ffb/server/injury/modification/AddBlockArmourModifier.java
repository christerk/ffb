package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.server.GameState;

import java.util.Collections;

public class AddBlockArmourModifier extends InjuryContextModification<AddBlockArmourModifierParams> {

	public AddBlockArmourModifier() {
		super(Collections.singleton(Block.class));
	}

	@Override
	protected AddBlockArmourModifierParams params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		return new AddBlockArmourModifierParams(gameState, newContext, injuryType);
	}

	@Override
	SkillUse skillUse() {
		return SkillUse.ADD_ARMOUR_MODIFIER;
	}
}
