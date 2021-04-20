package com.fumbbl.ffb.server.step.generator.bb2020;

import static com.fumbbl.ffb.server.step.StepParameter.from;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

@RulesCollection(RulesCollection.Rules.BB2020)
public class EndPlayerAction extends com.fumbbl.ffb.server.step.generator.EndPlayerAction {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push endPlayerActionSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.REMOVE_BLITZ_STATE);
		sequence.add(StepId.INIT_FEEDING, from(StepParameterKey.GOTO_LABEL_ON_END, IStepLabel.END_FEEDING),
			from(StepParameterKey.FEEDING_ALLOWED, params.isFeedingAllowed()),
			from(StepParameterKey.END_PLAYER_ACTION, params.isEndPlayerAction()), from(StepParameterKey.END_TURN, params.isEndTurn()));
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.FEEDING));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_FEEDING, IStepLabel.END_FEEDING);
		// inserts select or inducement sequence at this point

		gameState.getStepStack().push(sequence.getSequence());
	}
}
