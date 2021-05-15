package com.fumbbl.ffb.server.step.generator.bb2020;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerLogLevel;
import com.fumbbl.ffb.server.step.IStepLabel;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.step.generator.Sequence;

import static com.fumbbl.ffb.server.step.StepParameter.from;

@RulesCollection(RulesCollection.Rules.BB2020)
public class ScatterPlayer extends com.fumbbl.ffb.server.step.generator.ScatterPlayer {

	@Override
	public void pushSequence(SequenceParams params) {
		GameState gameState = params.getGameState();
		gameState.getServer().getDebugLog().log(IServerLogLevel.DEBUG, gameState.getId(),
			"push scatterPlayerSequence onto stack");

		Sequence sequence = new Sequence(gameState);

		if (params.hasSwoop()) {
			sequence.add(StepId.SWOOP, from(StepParameterKey.THROWN_PLAYER_ID, params.getThrownPlayerId()),
				from(StepParameterKey.THROWN_PLAYER_STATE, params.getThrownPlayerState()),
				from(StepParameterKey.THROWN_PLAYER_HAS_BALL, params.isThrownPlayerHasBall()),
				from(StepParameterKey.THROWN_PLAYER_COORDINATE, params.getThrownPlayerCoordinate()),
				from(StepParameterKey.THROW_SCATTER, params.isThrowScatter()),
				from(StepParameterKey.GOTO_LABEL_ON_FALL_DOWN, IStepLabel.APOTHECARY_HIT_PLAYER));
		} else {
			sequence.add(StepId.INIT_SCATTER_PLAYER, from(StepParameterKey.THROWN_PLAYER_ID, params.getThrownPlayerId()),
				from(StepParameterKey.THROWN_PLAYER_STATE, params.getThrownPlayerState()),
				from(StepParameterKey.THROWN_PLAYER_HAS_BALL, params.isThrownPlayerHasBall()),
				from(StepParameterKey.THROWN_PLAYER_COORDINATE, params.getThrownPlayerCoordinate()),
				from(StepParameterKey.THROW_SCATTER, params.isThrowScatter()));
		}
		sequence.add(StepId.PLACE_BALL);
		sequence.add(StepId.APOTHECARY, IStepLabel.APOTHECARY_HIT_PLAYER,
			from(StepParameterKey.APOTHECARY_MODE, ApothecaryMode.HIT_PLAYER));
		sequence.add(StepId.CATCH_SCATTER_THROW_IN);
		sequence.add(StepId.END_SCATTER_PLAYER);
		// may insert a new scatterPlayerSequence at this point

		gameState.getStepStack().push(sequence.getSequence());

	}
}
