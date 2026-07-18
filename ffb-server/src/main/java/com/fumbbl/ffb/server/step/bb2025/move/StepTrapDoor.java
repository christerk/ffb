package com.fumbbl.ffb.server.step.bb2025.move;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.GameState;

/**
 * BB2025 variant of the trap door step. An acting team player falling through
 * the trap door always causes a turnover, regardless of ball possession.
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepTrapDoor extends com.fumbbl.ffb.server.step.mixed.move.StepTrapDoor {

	public StepTrapDoor(GameState pGameState) {
		super(pGameState);
	}

	@Override
	protected boolean fallCausesTurnover(Game game, Player<?> player, boolean hasBall) {
		return game.getActingTeam().hasPlayer(player);
	}
}
