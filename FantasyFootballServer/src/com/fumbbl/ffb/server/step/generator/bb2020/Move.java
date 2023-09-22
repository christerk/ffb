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
public class Move extends com.fumbbl.ffb.server.step.generator.Move {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();

		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(), "push moveSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_MOVING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING),
			from(StepParameterKey.MOVE_STACK, params.getMoveStack()), from(StepParameterKey.GAZE_VICTIM_ID, params.getGazeVictimId()));
		sequence.add(StepId.INIT_ACTIVATION);
		sequence.add(StepId.ANIMAL_SAVAGERY,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
		sequence.add(StepId.PLACE_BALL);
		sequence.add(StepId.APOTHECARY,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ANIMAL_SAVAGERY));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.SET_DEFENDER, from(StepParameterKey.BLOCK_DEFENDER_ID, params.getGazeVictimId()),
			from(StepParameterKey.IGNORE_NULL_VALUE, true));
		sequence.add(StepId.GOTO_LABEL, from(StepParameterKey.GOTO_LABEL, IStepLabel.NEXT), from(StepParameterKey.ALTERNATE_GOTO_LABEL, IStepLabel.END_MOVING));
		sequence.add(StepId.BONE_HEAD, IStepLabel.NEXT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.TAKE_ROOT);
		sequence.add(StepId.UNCHANNELLED_FURY, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.BLOOD_LUST);
		sequence.add(StepId.HYPNOTIC_GAZE, IStepLabel.HYPNOTIC_GAZE,
			from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING));
		sequence.add(StepId.MOVE_BALL_AND_CHAIN, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING),
			from(StepParameterKey.GOTO_LABEL_ON_FALL_DOWN, IStepLabel.FALL_DOWN));
		sequence.add(StepId.MOVE);
		// Do GFI twice to deal with Ball and Chain separately.
		sequence.add(StepId.GO_FOR_IT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.GO_FOR_IT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN),
			from(StepParameterKey.BALL_AND_CHAIN_GFI, true));
		sequence.add(StepId.TENTACLES, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.END_MOVING));
		sequence.add(StepId.JUMP, from(StepParameterKey.MOVE_START, params.getMoveStart()), from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.MOVE_DODGE, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.DIVING_TACKLE, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.RETRY_DODGE));
		sequence.jump(IStepLabel.SHADOWING);
		sequence.add(StepId.MOVE_DODGE, IStepLabel.RETRY_DODGE,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
		sequence.add(StepId.DROP_DIVING_TACKLER);
		sequence.add(StepId.SHADOWING, IStepLabel.SHADOWING);
		sequence.add(StepId.PICK_UP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.SCATTER_BALL));
		sequence.jump(IStepLabel.SCATTER_BALL);
		sequence.add(StepId.DROP_DIVING_TACKLER, IStepLabel.FALL_DOWN);
		sequence.add(StepId.SHADOWING); // falling player can be shadowed
		sequence.add(StepId.FALL_DOWN);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.DEFENDER));
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ATTACKER));

		sequence.add(StepId.TRAP_DOOR, IStepLabel.SCATTER_BALL);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.TRAP_DOOR));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);

		sequence.add(StepId.END_MOVING, IStepLabel.END_MOVING);
		// may insert endTurn or block sequence add this point

		gameState.getStepStack().push(sequence.getSequence());
	}
}
