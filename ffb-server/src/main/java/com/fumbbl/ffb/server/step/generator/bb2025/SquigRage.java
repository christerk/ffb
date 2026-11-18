package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2025)
public class SquigRage extends com.fumbbl.ffb.server.step.generator.SquigRage {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push squigRageSequence onto stack");

		Sequence sequence = new Sequence(gameState);
		sequence.add(StepId.INIT_ACTIVATION);
		sequence.add(StepId.SQUIG_RAGE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, params.getFailureLabel()));
		sequence.add(StepId.STEADY_FOOTING);
		sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
		sequence.add(StepId.PLACE_BALL);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.SQUIG_RAGE));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_SQUIG_RAGE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, params.getFailureLabel()));
		gameState.getStepStack().push(sequence.getSequence());
	}
}
