package com.fumbbl.ffb.server.step.generator.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

@RulesCollection(RulesCollection.Rules.COMMON)
public class EndTurn extends SequenceGenerator<SequenceGenerator.SequenceParams> {

	public EndTurn() {
		super(Type.EndTurn);
	}

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
