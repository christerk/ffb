package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ThenIStartedBlastin extends com.fumbbl.ffb.server.step.generator.ThenIStartedBlastin {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push started blastin onto stack");

		Sequence sequence = new Sequence(gameState);
		ActivationSequenceBuilder.create().withFailureLabel(IStepLabel.END).addTo(sequence);

		sequence.add(StepId.THEN_I_STARTED_BLASTIN, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END));
		sequence.add(StepId.STEADY_FOOTING, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.END));
		sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
		sequence.add(StepId.APOTHECARY,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_THEN_I_STARTED_BLASTIN, IStepLabel.END);
		gameState.getStepStack().push(sequence.getSequence());
	}
}
