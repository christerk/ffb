package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;

import java.util.Collections;

public class ToxinConnoisseurModification extends InjuryContextModification<ModificationParams> {

	public ToxinConnoisseurModification() {
		super(Collections.singleton(Stab.class));
	}

	@Override
	protected ModificationParams params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		return new ModificationParams(gameState, newContext, injuryType);
	}

	@Override
	protected boolean tryInjuryModification(Game game, InjuryContext injuryContext, InjuryType injuryType) {
		return !injuryContext.isCasualty();
	}

	@Override
	SkillUse skillUse() {
		return SkillUse.ADD_INJURY_MODIFIER;
	}
}
