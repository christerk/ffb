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
public class BlackInk extends com.fumbbl.ffb.server.step.generator.BlackInk {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push blackInkSequence onto stack");

		Sequence sequence = new Sequence(gameState);
		ActivationSequenceBuilder.create().withFailureLabel(IStepLabel.END).addTo(sequence);

		sequence.add(StepId.FOUL_APPEARANCE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END));
		sequence.add(StepId.BLACK_INK, IStepLabel.END, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, params.getGoToLabelFailure()),
			from(StepParameterKey.OLD_PLAYER_STATE, params.getOldPlayerState()));

		gameState.getStepStack().push(sequence.getSequence());
	}
}
