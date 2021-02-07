package com.balancedbytes.games.ffb.server.step.generator.common;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.IStepLabel;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;
import com.balancedbytes.games.ffb.server.step.generator.Sequence;
import com.balancedbytes.games.ffb.server.step.generator.SequenceGenerator;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.COMMON)
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
			from(StepParameterKey.CATCHER_ID, params.catcherId), from(StepParameterKey.PASS_FUMBLE, params.passFumble));
		// may insert multiple specialEffect sequences add this point
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_BOMB, IStepLabel.END_BOMB);
		// may insert endPlayerAction or pass sequence add this point
		gameState.getStepStack().push(sequence.getSequence());
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final String catcherId;
		private final boolean passFumble;

		public SequenceParams(GameState gameState, String catcherId, boolean passFumble) {
			super(gameState);
			this.catcherId = catcherId;
			this.passFumble = passFumble;
		}
	}
}
