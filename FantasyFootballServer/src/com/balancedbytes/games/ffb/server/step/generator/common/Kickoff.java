package com.balancedbytes.games.ffb.server.step.generator.common;

import com.balancedbytes.games.ffb.ApothecaryMode;
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
public class Kickoff extends SequenceGenerator<Kickoff.SequenceParams> {

	public Kickoff() {
		super(Type.Kickoff);
	}

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push kickoffSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		if (params.withCoinChoice) {
			sequence.add(StepId.COIN_CHOICE);
			sequence.add(StepId.RECEIVE_CHOICE);
		}
		sequence.add(StepId.INIT_KICKOFF);
		// inserts inducement sequence at this point
		sequence.add(StepId.SETUP, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICKOFF));
		// inserts inducement sequence at this point
		sequence.add(StepId.SETUP, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICKOFF));
		sequence.add(StepId.KICKOFF);
		sequence.add(StepId.KICKOFF_SCATTER_ROLL);
		sequence.add(StepId.SWARMING, from(StepParameterKey.HANDLE_RECEIVING_TEAM, false));
		sequence.add(StepId.SWARMING, from(StepParameterKey.HANDLE_RECEIVING_TEAM, true));
		sequence.add(StepId.KICKOFF_RETURN);
		// may insert select sequence at this point
		sequence.add(StepId.KICKOFF_RESULT_ROLL);
		sequence.add(StepId.APPLY_KICKOFF_RESULT, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICKOFF),
			from(StepParameterKey.GOTO_LABEL_ON_BLITZ, IStepLabel.BLITZ_TURN));
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.HOME));
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.AWAY));
		sequence.jump(IStepLabel.KICKOFF_ANIMATION);
		sequence.add(StepId.BLITZ_TURN, IStepLabel.BLITZ_TURN);
		// may insert selectSequence at this point
		sequence.add(StepId.KICKOFF_ANIMATION, IStepLabel.KICKOFF_ANIMATION);
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.TOUCHBACK);
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_KICKOFF, IStepLabel.END_KICKOFF);
		// continues with endTurnSequence after that

		gameState.getStepStack().push(sequence.getSequence());
	}

	public static class SequenceParams extends SequenceGenerator.SequenceParams {
		private final boolean withCoinChoice;

		public SequenceParams(GameState gameState, boolean withCoinChoice) {
			super(gameState);
			this.withCoinChoice = withCoinChoice;
		}
	}
}
