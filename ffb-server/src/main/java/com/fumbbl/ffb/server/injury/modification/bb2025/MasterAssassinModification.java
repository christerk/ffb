package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.injury.modification.InjuryContextModification;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;

import java.util.Collections;

public class MasterAssassinModification extends InjuryContextModification<ModificationParams> {

	public MasterAssassinModification() {
		super(Collections.singleton(Stab.class));
	}

	@Override
	protected ModificationParams params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		return new ModificationParams(gameState, newContext, injuryType);
	}

	@Override
	protected boolean tryArmourRollModification(ModificationParams params) {
		return !params.getNewContext().isArmorBroken();
	}

	@Override
	protected boolean modifyArmourInternal(ModificationParams params) {
		params.getNewContext().setArmorRoll(params.getGameState().getDiceRoller().rollArmour());
		return true;
	}

	@Override
	protected SkillUse skillUse() {
		return SkillUse.RE_ROLL_ARMOUR;
	}
}
