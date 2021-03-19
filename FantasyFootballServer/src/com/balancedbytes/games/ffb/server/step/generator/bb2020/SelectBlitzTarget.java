package com.balancedbytes.games.ffb.server.step.generator.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStepLabel;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.generator.Sequence;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
public class SelectBlitzTarget extends com.balancedbytes.games.ffb.server.step.generator.SelectBlitzTarget {
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
		sequence.add(StepId.SELECT_BLITZ_TARGET_END, IStepLabel.END_BLITZING);

		gameState.getStepStack().push(sequence.getSequence());
	}
}
