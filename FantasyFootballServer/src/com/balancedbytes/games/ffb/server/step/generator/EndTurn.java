package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.StepId;

@RulesCollection(RulesCollection.Rules.COMMON)
public class EndTurn extends SequenceGenerator<SequenceGenerator.SequenceParams> {

	protected EndTurn() {
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
