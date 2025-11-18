package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
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
		ActivationSequenceBuilder.create().withFailureLabel(IStepLabel.END_SELECTING).addTo(sequence);

		sequence.add(StepId.GOTO_LABEL, from(StepParameterKey.GOTO_LABEL, IStepLabel.NEXT), from(StepParameterKey.ALTERNATE_GOTO_LABEL, IStepLabel.END_SELECTING));
		sequence.add(StepId.JUMP_UP, IStepLabel.NEXT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.STAND_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		PlayerAction playerAction = gameState.getGame().getActingPlayer().getPlayerAction();
		sequence.add(StepId.RESET_FUMBLEROOSKIE, IStepLabel.END_SELECTING,
			from(StepParameterKey.IN_SELECT, true),
			from(StepParameterKey.RESET_FOR_FAILED_BLOCK, playerAction != null && playerAction.isBlitzMove()));
		sequence.add(StepId.END_SELECTING, from(StepParameterKey.BLOCK_TARGETS, params.getBlockTargets()));
		// may insert endTurn, pass, throwTeamMate, block, foul or moveSequence add
		// this point

		gameState.getStepStack().push(sequence.getSequence());
	}
}
