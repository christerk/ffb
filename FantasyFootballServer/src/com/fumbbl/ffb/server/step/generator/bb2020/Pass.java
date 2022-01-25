package com.fumbbl.ffb.server.step.generator.bb2020;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.skillbehaviour.StepHook;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
public class Pass extends com.fumbbl.ffb.server.step.generator.Pass {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(), "push passSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_PASSING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING),
			from(StepParameterKey.TARGET_COORDINATE, params.getTargetCoordinate()));
		sequence.add(StepId.ANIMAL_SAVAGERY,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
		sequence.add(StepId.PLACE_BALL);
		sequence.add(StepId.APOTHECARY,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.ANIMAL_SAVAGERY));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.BONE_HEAD, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.REALLY_STUPID, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.TAKE_ROOT);
		sequence.add(StepId.UNCHANNELLED_FURY, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.RECOVER_FROM_GAZE);
		sequence.add(StepId.BOMBARDIER);
		sequence.add(StepId.ANIMOSITY, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_PASSING));
		sequence.add(StepId.PASS_BLOCK, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING));
		sequence.add(StepId.DISPATCH_PASSING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_PASSING),
			from(StepParameterKey.GOTO_LABEL_ON_HAND_OVER, IStepLabel.HAND_OVER),
			from(StepParameterKey.GOTO_LABEL_ON_HAIL_MARY_PASS, IStepLabel.HAIL_MARY_PASS));

		sequence.add(StepId.PASS, IStepLabel.PASS, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.INTERCEPT),
			from(StepParameterKey.GOTO_LABEL_ON_MISSED_PASS, IStepLabel.MISSED_PASS),
			from(StepParameterKey.GOTO_LABEL_ON_SAVED_FUMBLE, IStepLabel.END_PASSING));
		sequence.jump(IStepLabel.SCATTER_BALL);
		sequence.add(StepId.HAIL_MARY_PASS, IStepLabel.HAIL_MARY_PASS,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.SCATTER_BALL));
		sequence.add(StepId.MISSED_PASS, IStepLabel.MISSED_PASS);
		sequence.add(StepId.INTERCEPT, IStepLabel.INTERCEPT, from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.RESOLVE_PASS));

		sequence.insertHooks(StepHook.HookPoint.PASS_INTERCEPT,
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.RESOLVE_PASS));

		sequence.add(StepId.RESOLVE_PASS, IStepLabel.RESOLVE_PASS);

		sequence.jump(IStepLabel.SCATTER_BALL);
		sequence.add(StepId.HAND_OVER, IStepLabel.HAND_OVER);
		sequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.SCATTER_BALL);

		sequence.add(StepId.END_PASSING, IStepLabel.END_PASSING);
		// may insert bomb or endPlayerAction sequence add this point

		gameState.getStepStack().push(sequence.getSequence());

	}

}
