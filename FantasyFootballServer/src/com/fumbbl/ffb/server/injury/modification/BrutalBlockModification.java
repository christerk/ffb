package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.server.GameState;

import java.util.Collections;

public class BrutalBlockModification extends InjuryContextModification<ModificationParams> {

	public BrutalBlockModification() {
		super(Collections.singleton(Block.class));
	}

	@Override
	protected ModificationParams params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		return new ModificationParams(gameState, newContext, injuryType);
	}

	@Override
	protected boolean modifyInjuryInternal(ModifiedInjuryContext injuryContext, GameState gameState) {
		injuryContext.addInjuryModifiers(getSkill().getInjuryModifiers());
		PlayerState newInjury = interpretInjury(gameState, injuryContext);

		if (!newInjury.equals(injuryContext.fInjury)) {
			injuryContext.setInjury(newInjury);
			return true;
		}

		return false;
	}

	@Override
	SkillUse skillUse() {
		return SkillUse.ADD_INJURY_MODIFIER;
	}
}
