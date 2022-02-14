package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.server.GameState;

public class AddBlockArmourModifierParams extends ModificationParams {
	public AddBlockArmourModifierParams(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		super(gameState, newContext, injuryType);
	}
}
