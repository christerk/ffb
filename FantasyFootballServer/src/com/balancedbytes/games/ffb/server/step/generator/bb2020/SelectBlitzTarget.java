package com.balancedbytes.games.ffb.server.step.generator.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.generator.Sequence;

@RulesCollection(RulesCollection.Rules.BB2020)
public class SelectBlitzTarget extends com.balancedbytes.games.ffb.server.step.generator.SelectBlitzTarget {
	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();

		Sequence sequence = new Sequence(gameState);
		sequence.add(StepId.SELECT_BLITZ_TARGET);

		gameState.getStepStack().push(sequence.getSequence());
	}
}
