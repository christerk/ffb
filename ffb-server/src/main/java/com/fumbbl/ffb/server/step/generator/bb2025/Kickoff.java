package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2025)
public class Kickoff extends com.fumbbl.ffb.server.step.generator.Kickoff {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push kickoffSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		if (params.isWithCoinChoice()) {
			sequence.add(StepId.COIN_CHOICE);
			sequence.add(StepId.RECEIVE_CHOICE);
		}
		sequence.add(StepId.INIT_KICKOFF);
		// inserts inducement sequence at this point
		sequence.add(StepId.SETUP);
		// inserts inducement sequence at this point
		sequence.add(StepId.SETUP);
		sequence.add(StepId.SWARMING, from(StepParameterKey.HANDLE_RECEIVING_TEAM, false));
		sequence.add(StepId.SWARMING, from(StepParameterKey.HANDLE_RECEIVING_TEAM, true));
		sequence.add(StepId.MASTER_CHEF);
		sequence.add(StepId.KICKOFF);
		if (UtilGameOption.isOptionEnabled(gameState.getGame(), GameOptionId.ASK_FOR_KICK_AFTER_ROLL)) {
			sequence.add(StepId.KICKOFF_SCATTER_ROLL_ASK_AFTER);
		} else {
			sequence.add(StepId.KICKOFF_SCATTER_ROLL);
		}
		sequence.add(StepId.KICKOFF_RETURN);
		// may insert select sequence at this point
		sequence.add(StepId.KICKOFF_RESULT_ROLL);
		sequence.add(StepId.APPLY_KICKOFF_RESULT, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_KICKOFF),
			from(StepParameterKey.GOTO_LABEL_ON_BLITZ, IStepLabel.BLITZ_TURN));
		// may insert send off steps at this point
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
}
