package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2025)
public class Punt extends com.fumbbl.ffb.server.step.generator.Punt {
	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push puntSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_PUNT, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END));
		ActivationSequenceBuilder.create().withFailureLabel(IStepLabel.END).addTo(sequence);
		sequence.add(StepId.PUNT);
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_PUNT, IStepLabel.END);

		gameState.getStepStack().push(sequence.getSequence());
	}
}
