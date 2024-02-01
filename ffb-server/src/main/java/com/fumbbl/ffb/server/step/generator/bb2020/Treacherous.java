package com.fumbbl.ffb.server.step.generator.bb2020;

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
public class Treacherous extends com.fumbbl.ffb.server.step.generator.Treacherous {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push treacherousSequence onto stack");

		Sequence sequence = new Sequence(gameState);
		sequence.add(StepId.INIT_ACTIVATION);
		sequence.add(StepId.ANIMAL_SAVAGERY,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END));
		sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
		sequence.add(StepId.PLACE_BALL);
		sequence.add(StepId.APOTHECARY,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ANIMAL_SAVAGERY));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END));
		sequence.add(StepId.TAKE_ROOT);
		sequence.add(StepId.UNCHANNELLED_FURY, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END));
		sequence.add(StepId.BLOOD_LUST);
		sequence.add(StepId.JUMP_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END));
		sequence.add(StepId.STAND_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END));
		sequence.add(StepId.TREACHEROUS, IStepLabel.END, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, params.getFailureLabel()));
		sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
		sequence.add(StepId.APOTHECARY,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		gameState.getStepStack().push(sequence.getSequence());
	}
}
