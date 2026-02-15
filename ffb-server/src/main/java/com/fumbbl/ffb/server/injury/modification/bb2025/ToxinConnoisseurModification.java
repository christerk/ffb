package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.StabForSpp;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.injury.modification.InjuryContextModification;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;

import java.util.HashSet;

public class ToxinConnoisseurModification extends InjuryContextModification<ModificationParams> {

	public ToxinConnoisseurModification() {
		super(new HashSet<Class<? extends InjuryType>>() {{
			add(Stab.class);
			add(StabForSpp.class);
		}});
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
	protected SkillUse skillUse() {
		return SkillUse.ADD_INJURY_MODIFIER;
	}
}
