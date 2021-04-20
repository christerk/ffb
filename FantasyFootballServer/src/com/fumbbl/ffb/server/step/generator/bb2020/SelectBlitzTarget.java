package com.fumbbl.ffb.server.step.generator.bb2020;

import static com.fumbbl.ffb.server.step.StepParameter.from;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

@RulesCollection(RulesCollection.Rules.BB2020)
public class SelectBlitzTarget extends com.fumbbl.ffb.server.step.generator.SelectBlitzTarget {
	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();

		Sequence sequence = new Sequence(gameState);
		sequence.add(StepId.SELECT_BLITZ_TARGET, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_BLITZING));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLITZING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLITZING));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLITZING));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLITZING));
		sequence.add(StepId.BLOOD_LUST, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLITZING));
		sequence.add(StepId.FOUL_APPEARANCE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_BLITZING));
		sequence.add(StepId.DUMP_OFF);
		sequence.add(StepId.SELECT_BLITZ_TARGET_END, IStepLabel.END_BLITZING);
		// might add END_MOVING here
		gameState.getStepStack().push(sequence.getSequence());
	}
}
