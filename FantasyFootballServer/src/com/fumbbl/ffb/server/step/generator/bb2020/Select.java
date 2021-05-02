package com.fumbbl.ffb.server.step.generator.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
public class Select extends com.fumbbl.ffb.server.step.generator.Select {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();

		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push selectSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_SELECTING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_SELECTING),
			from(StepParameterKey.UPDATE_PERSISTENCE, params.isUpdatePersistence()));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.BLOOD_LUST, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.JUMP_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.STAND_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.RESET_FUMBLEROOSKIE, IStepLabel.END_SELECTING, StepParameter.from(StepParameterKey.CHECK_PLAYER_ACTION, true));
		sequence.add(StepId.END_SELECTING);
		// may insert endTurn, pass, throwTeamMate, block, foul or moveSequence add
		// this point

		gameState.getStepStack().push(sequence.getSequence());
	}
}
