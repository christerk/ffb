package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.generator.Sequence;

@RulesCollection(RulesCollection.Rules.BB2025)
public class Punt extends com.fumbbl.ffb.server.step.generator.Punt {
	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push puntSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_PUNT);
		ActivationSequenceBuilder.create().withFailureLabel(IStepLabel.END).addTo(sequence);
		sequence.add(StepId.PUNT);
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_PUNT);

		gameState.getStepStack().push(sequence.getSequence());
	}
}
