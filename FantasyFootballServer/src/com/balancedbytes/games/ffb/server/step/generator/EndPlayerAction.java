package com.balancedbytes.games.ffb.server.step.generator;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerLogLevel;
import com.balancedbytes.games.ffb.server.step.IStepLabel;
import com.balancedbytes.games.ffb.server.step.StepId;
import com.balancedbytes.games.ffb.server.step.StepParameterKey;

import static com.balancedbytes.games.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.COMMON)
public class EndPlayerAction extends SequenceGenerator<EndPlayerAction.SequenceParams> {

	protected EndPlayerAction() {
		super(Type.EndPlayerAction);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push endPlayerActionSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.INIT_FEEDING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FEEDING),
			from(StepParameterKey.FEEDING_ALLOWED, params.feedingAllowed),
			from(StepParameterKey.END_PLAYER_ACTION, params.endPlayerAction), from(StepParameterKey.END_TURN, params.endTurn));
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.FEEDING));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_FEEDING, IStepLabel.END_FEEDING);
		// inserts select or inducement sequence at this point

		gameState.getStepStack().push(sequence.getSequence());
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final boolean feedingAllowed, endPlayerAction, endTurn;

		public SequenceParams(GameState gameState, boolean feedingAllowed, boolean endPlayerAction, boolean endTurn) {
			super(gameState);
			this.feedingAllowed = feedingAllowed;
			this.endPlayerAction = endPlayerAction;
			this.endTurn = endTurn;
		}

	}
}
