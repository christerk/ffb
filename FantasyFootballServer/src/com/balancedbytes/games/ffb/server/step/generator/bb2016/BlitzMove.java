package com.balancedbytes.games.ffb.server.step.generator.bb2016;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStepLabel;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.generator.Sequence;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2016)
public class BlitzMove extends com.balancedbytes.games.ffb.server.step.generator.BlitzMove {
	public BlitzMove() {
		super(Type.BlitzMove);
	}

	@Override
	public void pushSequence(BlitzMove.SequenceParams params) {
		GameState gameState = params.getGameState();

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_MOVING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_MOVING),
				from(StepParameterKey.MOVE_STACK, params.getMoveStack()), from(StepParameterKey.GAZE_VICTIM_ID, params.getGazeVictimId()));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
		sequence.add(StepId.BLOOD_LUST, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_MOVING));
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
		sequence.add(StepId.JUMP, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.FALL_DOWN));
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
		sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.SCATTER_BALL);
		sequence.add(StepId.END_MOVING, IStepLabel.END_MOVING);
		// may insert endTurn or block sequence add this point

		gameState.getStepStack().push(sequence.getSequence());
	}
}
