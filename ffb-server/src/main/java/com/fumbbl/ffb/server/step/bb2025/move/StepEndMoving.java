package com.fumbbl.ffb.server.step.bb2025.move;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StepEndMoving extends com.fumbbl.ffb.server.step.bb2020.move.StepEndMoving {

	public StepEndMoving(GameState gameState) {
		super(gameState);
	}
}

