package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.generator.Sequence;

@RulesCollection(RulesCollection.Rules.BB2025)
public class IllCarryYou extends com.fumbbl.ffb.server.step.generator.IllCarryYou {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push illCarryYouSequence onto stack");

		Sequence sequence = new Sequence(gameState);
		sequence.add(StepId.ILL_CARRY_YOU);

		gameState.getStepStack().push(sequence.getSequence());
	}
}