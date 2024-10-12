package com.fumbbl.ffb.server.step.generator.bb2020;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.server.step.generator.SequenceGenerator;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
public class QuickBite extends com.fumbbl.ffb.server.step.generator.QuickBite {
	@Override
	public void pushSequence(SequenceGenerator.SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(), "push quickBite onto stack");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.QUICK_BITE);
		sequence.add(StepId.HANDLE_DROP_PLAYER_CONTEXT);
		sequence.add(StepId.APOTHECARY,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.QUICK_BITE));
		gameState.getStepStack().push(sequence.getSequence());
	}
}
