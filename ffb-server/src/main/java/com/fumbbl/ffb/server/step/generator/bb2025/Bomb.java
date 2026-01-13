package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2025)
public class Bomb extends SequenceGenerator<Bomb.SequenceParams> {

	public Bomb() {
		super(Type.Bomb);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(), "push bombSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_BOMB, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_BOMB),
			from(StepParameterKey.CATCHER_ID, params.catcherId), from(StepParameterKey.PASS_FUMBLE, params.passFumble),
			from(StepParameterKey.DONT_DROP_FUMBLE, params.dontDropFumble));
		// may insert multiple specialEffect sequences add this point
		sequence.add(StepId.CATCH_SCATTER_THROW_IN); // handles the bomb bounce
		sequence.add(StepId.RESOLVE_BOMB);
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_BOMB, IStepLabel.END_BOMB, from(StepParameterKey.ALLOW_MOVE_AFTER_PASS, params.allowMoveAfterPass));
		// may insert endPlayerAction or pass sequence add this point
		gameState.getStepStack().push(sequence.getSequence());
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final String catcherId;
		private final boolean passFumble, allowMoveAfterPass, dontDropFumble;

		public SequenceParams(GameState gameState, String catcherId, boolean passFumble, boolean allowMoveAfterPass, boolean dontDropFumble) {
			super(gameState);
			this.catcherId = catcherId;
			this.passFumble = passFumble;
			this.allowMoveAfterPass = allowMoveAfterPass;
			this.dontDropFumble = dontDropFumble;
		}
	}
}
