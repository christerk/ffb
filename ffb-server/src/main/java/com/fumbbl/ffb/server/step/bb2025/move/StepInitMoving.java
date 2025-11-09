package com.fumbbl.ffb.server.step.bb2025.move;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepInitMoving extends com.fumbbl.ffb.server.step.bb2020.move.StepInitMoving {

	public StepInitMoving(GameState gameState) {
		super(gameState);
	}
}

