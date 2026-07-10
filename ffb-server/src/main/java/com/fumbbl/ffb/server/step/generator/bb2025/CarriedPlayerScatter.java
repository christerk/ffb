package com.fumbbl.ffb.server.step.generator.bb2025;

import com.fumbbl.ffb.ApothecaryMode;
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
public class CarriedPlayerScatter extends com.fumbbl.ffb.server.step.generator.CarriedPlayerScatter {

	@Override
	public void pushSequence(SequenceGenerator.SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push CarriedPlayerScatterSequence onto stack");

		Sequence parentSequence = new Sequence(gameState);
		parentSequence.add(StepId.STEADY_FOOTING, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.HIT_PLAYER));
		parentSequence.add(StepId.PLACE_BALL);
		parentSequence.add(StepId.APOTHECARY, from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.HIT_PLAYER));
		parentSequence.add(StepId.CATCH_SCATTER_THROW_IN);

		parentSequence.add(StepId.RIGHT_STUFF, IStepLabel.RIGHT_STUFF,
			from(StepParameterKey.IS_KICKED_PLAYER, false),
			from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.END_SCATTER_PLAYER));
		parentSequence.add(StepId.STEADY_FOOTING, from(StepParameterKey.GOTO_LABEL_ON_SUCCESS, IStepLabel.END_SCATTER_PLAYER));

		parentSequence.jump(IStepLabel.APOTHECARY_THROWN_PLAYER);
		parentSequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_THROWN_PLAYER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.THROWN_PLAYER));
		parentSequence.add(StepId.CATCH_SCATTER_THROW_IN, IStepLabel.END_SCATTER_PLAYER);

		gameState.getStepStack().push(parentSequence.getSequence());
	}
}
