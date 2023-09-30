package com.fumbbl.ffb.server.injury.modification;

public class SlayerModification extends AvOrInjModification {

	@Override
	protected boolean tryArmourRollModification(ModificationParams params) {
		return super.tryArmourRollModification(params)
			&& params.getGameState().getGame().getPlayerById(params.getNewContext().fDefenderId)
			.getStrengthWithModifiers() >= 5;
	}

}
