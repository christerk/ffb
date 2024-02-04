package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;

import java.util.HashSet;

public class MasterAssassinModification extends InjuryContextModification<ModificationParams> {

	public MasterAssassinModification() {
		super(new HashSet<Class<? extends InjuryType>>() {{
			add(Stab.class);
		}});
	}

	@Override
	protected ModificationParams params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		return new ModificationParams(gameState, newContext, injuryType);
	}

	@Override
	protected boolean modifyInjuryInternal(ModifiedInjuryContext injuryContext, GameState gameState) {
		injuryContext.setInjuryRoll(gameState.getDiceRoller().rollInjury());
		injuryContext.setInjury(interpretInjury(gameState, injuryContext));

		return true;
	}

	@Override
	protected boolean tryInjuryModification(Game game, InjuryContext injuryContext, InjuryType injuryType) {
		return !injuryContext.isCasualty();
	}

	@Override
	SkillUse skillUse() {
		return SkillUse.RE_ROLL_INJURY;
	}
}
