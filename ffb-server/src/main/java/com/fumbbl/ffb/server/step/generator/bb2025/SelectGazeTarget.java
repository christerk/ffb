package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2025)
public class SelectGazeTarget extends com.fumbbl.ffb.server.step.generator.SelectGazeTarget {
	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();

		Sequence sequence = new Sequence(gameState);
		sequence.add(StepId.SELECT_GAZE_TARGET, IStepLabel.SELECT, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_SELECTING));
		ActivationSequenceBuilder.create().withFailureLabel(IStepLabel.END_SELECTING).addTo(sequence);

		sequence.add(StepId.FOUL_APPEARANCE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.JUMP_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.STAND_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.SELECT_GAZE_TARGET_END, IStepLabel.END_SELECTING);
		// might add END_MOVING here
		gameState.getStepStack().push(sequence.getSequence());
	}
}
