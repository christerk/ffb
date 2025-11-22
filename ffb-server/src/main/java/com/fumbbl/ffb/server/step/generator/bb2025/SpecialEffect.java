package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2025)
public class SpecialEffect extends com.fumbbl.ffb.server.step.generator.SpecialEffect {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push specialEffectSequence onto stack (player " + params.getPlayerId() + ")");

		Sequence sequence = new Sequence(gameState);

		sequence.add(StepId.SPECIAL_EFFECT, from(StepParameterKey.SPECIAL_EFFECT, params.getSpecialEffect()),
			from(StepParameterKey.PLAYER_ID, params.getPlayerId()), from(StepParameterKey.ROLL_FOR_EFFECT, params.isRollForEffect()),
			from(StepParameterKey.GOTO_LABEL_ON_FAILURE, IStepLabel.END_SPECIAL_EFFECT));
		sequence.add(StepId.STEADY_FOOTING, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.END_SPECIAL_EFFECT));
		sequence.add(StepId.PLACE_BALL);
		sequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.SPECIAL_EFFECT));
		sequence.add(StepId.NEXT_STEP, IStepLabel.END_SPECIAL_EFFECT);

		gameState.getStepStack().push(sequence.getSequence());

	}

}
