package com.fumbbl.ffb.server.step.bb2020.ttm;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.InjuryType.InjuryTypeServer;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepParameter;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.server.util.UtilServerInjury;

public class TtmToCrowdHandler {
	public void handle(Game game, IStep step, Player<?> thrownPlayer,
	                   FieldCoordinate endCoordinate, boolean hasBall, InjuryTypeServer<?> injuryTypeServer) {
		// throw player out of bounds
		game.getFieldModel().setPlayerState(thrownPlayer, new PlayerState(PlayerState.FALLING));
		InjuryResult injuryResult = UtilServerInjury.handleInjury(step, injuryTypeServer, null,
			thrownPlayer, endCoordinate, null, null, ApothecaryMode.THROWN_PLAYER);
		step.publishParameter(new StepParameter(StepParameterKey.INJURY_RESULT, injuryResult));
		if (hasBall) {
			step.publishParameter(
				new StepParameter(StepParameterKey.CATCH_SCATTER_THROW_IN_MODE, CatchScatterThrowInMode.THROW_IN));
			step.publishParameter(new StepParameter(StepParameterKey.THROW_IN_COORDINATE, endCoordinate));
			step.publishParameter(new StepParameter(StepParameterKey.END_TURN, true));
		}
		// end loop
		step.publishParameter(new StepParameter(StepParameterKey.THROWN_PLAYER_COORDINATE, null));
	}
}
