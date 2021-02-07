package com.balancedbytes.games.ffb.server.step.generator.common;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.generator.Sequence;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;

@RulesCollection(RulesCollection.Rules.COMMON)
public class Wizard extends SequenceGenerator<SequenceGenerator.SequenceParams> {

	public Wizard() {
		super(Type.Wizard);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push wizardSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.WIZARD);
		// may insert multiple specialEffect sequences at this point
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);

		gameState.getStepStack().push(sequence.getSequence());
	}
}
