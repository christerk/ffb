package com.fumbbl.ffb.server.step.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.StepAction;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepPickUp extends com.fumbbl.ffb.server.step.bb2020.StepPickUp {

	public StepPickUp(GameState gameState) {
		super(gameState);
	}

	@Override
	public void start() {
		if (getGameState().getGame().getActingPlayer().getPlayer().hasSkillProperty(NamedProperties.movesRandomly)) {
			getResult().setNextAction(StepAction.NEXT_STEP);
		} else {
			super.start();
		}
	}
}

