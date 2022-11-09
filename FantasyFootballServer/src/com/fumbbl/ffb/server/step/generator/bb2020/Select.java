package com.fumbbl.ffb.server.step.generator.bb2020;

import com.fumbbl.ffb.ApothecaryMode;
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
		sequence.add(StepId.INIT_ACTIVATION);
		sequence.add(StepId.ANIMAL_SAVAGERY,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
		sequence.add(StepId.PLACE_BALL);
		sequence.add(StepId.APOTHECARY,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ANIMAL_SAVAGERY));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.GOTO_LABEL, from(StepParameterKey.GOTO_LABEL, IStepLabel.NEXT), from(StepParameterKey.ALTERNATE_GOTO_LABEL, IStepLabel.END_SELECTING));
		sequence.add(StepId.BONE_HEAD, IStepLabel.NEXT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.TAKE_ROOT);
		sequence.add(StepId.UNCHANNELLED_FURY, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.GOTO_LABEL, from(StepParameterKey.GOTO_LABEL, IStepLabel.NEXT), from(StepParameterKey.ALTERNATE_GOTO_LABEL, IStepLabel.END_SELECTING));
		sequence.add(StepId.JUMP_UP, IStepLabel.NEXT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.STAND_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SELECTING));
		sequence.add(StepId.RESET_FUMBLEROOSKIE, IStepLabel.END_SELECTING,
			from(StepParameterKey.IN_SELECT, true),
			from(StepParameterKey.RESET_FOR_FAILED_BLOCK, gameState.getGame().getActingPlayer().getPlayerAction() == PlayerAction.BLITZ_MOVE));
		sequence.add(StepId.END_SELECTING, from(StepParameterKey.BLOCK_TARGETS, params.getBlockTargets()));
		// may insert endTurn, pass, throwTeamMate, block, foul or moveSequence add
		// this point

		gameState.getStepStack().push(sequence.getSequence());
	}
}
