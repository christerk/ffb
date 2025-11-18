package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
public class LookIntoMyEyes extends com.fumbbl.ffb.server.step.generator.LookIntoMyEyes {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push lookIntoMyEyesSequence onto stack");

		Sequence sequence = new Sequence(gameState);
		ActivationSequenceBuilder.create().withFailureLabel(IStepLabel.END).addTo(sequence);

		sequence.add(StepId.INIT_LOOK_INTO_MY_EYES);
		sequence.add(StepId.FOUL_APPEARANCE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END));
		sequence.add(StepId.LOOK_INTO_MY_EYES, IStepLabel.END, from(StepParameterKey.PUSH_SELECT, params.isPushSelect()), from(StepParameterKey.GOTO_LABEL_ON_END, params.getGotoOnEnd()));

		gameState.getStepStack().push(sequence.getSequence());
	}
}
