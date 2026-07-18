package com.fumbbl.ffb.server.step.bb2025.move;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;

/**
 * BB2025 variant of the ball and chain move step. An acting team player moving
 * off the pitch (into the crowd) always causes a turnover.
 */
@RulesCollection(RulesCollection.Rules.BB2025)
public class StepMoveBallAndChain extends com.fumbbl.ffb.server.step.mixed.move.StepMoveBallAndChain {

	public StepMoveBallAndChain(GameState pGameState) {
		super(pGameState);
	}

	@Override
	protected boolean leavingPitchCausesTurnover(Game game, ActingPlayer actingPlayer) {
		return game.getActingTeam().hasPlayer(actingPlayer.getPlayer());
	}
}
