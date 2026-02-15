package com.fumbbl.ffb.server.step.generator.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.generator.Sequence;

@RulesCollection(RulesCollection.Rules.BB2016)
@RulesCollection(RulesCollection.Rules.BB2020)
public class EndTurn extends com.fumbbl.ffb.server.step.generator.EndTurn {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push endTurnSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.END_TURN);
		// may insert new sequence at this point

		gameState.getStepStack().push(sequence.getSequence());
	}
}
