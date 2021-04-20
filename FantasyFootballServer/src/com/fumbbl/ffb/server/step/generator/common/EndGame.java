package com.fumbbl.ffb.server.step.generator.common;

import static com.fumbbl.ffb.server.step.StepParameter.from;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

@RulesCollection(RulesCollection.Rules.COMMON)
public class EndGame extends SequenceGenerator<EndGame.SequenceParams> {

	public EndGame() {
		super(Type.EndGame);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();

		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push endGameSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_END_GAME, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_GAME),
			from(StepParameterKey.ADMIN_MODE, params.adminMode));
		sequence.add(StepId.PENALTY_SHOOTOUT);
		sequence.add(StepId.MVP);
		sequence.add(StepId.WINNINGS);
		sequence.add(StepId.FAN_FACTOR);
		sequence.add(StepId.PLAYER_LOSS);
		sequence.add(StepId.END_GAME, IStepLabel.END_GAME);

		gameState.getStepStack().push(sequence.getSequence());
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final boolean adminMode;

		public SequenceParams(GameState gameState, boolean adminMode) {
			super(gameState);
			this.adminMode = adminMode;
		}
	}
}
