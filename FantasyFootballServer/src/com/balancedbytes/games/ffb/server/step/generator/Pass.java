package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.skillbehaviour.StepHook;
import com.balancedbytes.games.ffb.server.step.IStepLabel;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.COMMON)
public class Pass extends SequenceGenerator<Pass.SequenceParams> {

	protected Pass() {
		super(Type.Pass);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(), "push passSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_PASSING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING),
			from(StepParameterKey.TARGET_COORDINATE, params.targetCoordinate));
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.TAKE_ROOT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.WILD_ANIMAL, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.BLOOD_LUST, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.BOMBARDIER);
		sequence.add(StepId.ANIMOSITY, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.PASS_BLOCK, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING));
		sequence.add(StepId.DISPATCH_PASSING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING),
			from(StepParameterKey.GOTO_LABEL_ON_HAND_OVER, IStepLabel.HAND_OVER),
			from(StepParameterKey.GOTO_LABEL_ON_HAIL_MARY_PASS, IStepLabel.HAIL_MARY_PASS));
		sequence.add(StepId.INTERCEPT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.PASS));

		sequence.insertHooks(StepHook.HookPoint.PASS_INTERCEPT,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));

		sequence.add(StepId.PASS, IStepLabel.PASS, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING),
			from(StepParameterKey.GOTO_LABEL_ON_MISSED_PASS, IStepLabel.MISSED_PASS));
		sequence.jump(IStepLabel.SCATTER_BALL);
		sequence.add(StepId.HAIL_MARY_PASS, IStepLabel.HAIL_MARY_PASS,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.SCATTER_BALL));
		sequence.add(StepId.MISSED_PASS, IStepLabel.MISSED_PASS);
		sequence.jump(IStepLabel.SCATTER_BALL);
		sequence.add(StepId.HAND_OVER, IStepLabel.HAND_OVER);
		sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.SCATTER_BALL);

		sequence.add(StepId.END_PASSING, IStepLabel.END_PASSING);
		// may insert bomb or endPlayerAction sequence add this point

		gameState.getStepStack().push(sequence.getSequence());

	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final FieldCoordinate targetCoordinate;

		public SequenceParams(GameState gameState, FieldCoordinate targetCoordinate) {
			super(gameState);
			this.targetCoordinate = targetCoordinate;
		}
	}

}
