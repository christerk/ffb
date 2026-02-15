package com.fumbbl.ffb.server.step.bb2025.command;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.DeferredCommandId;
import com.fumbbl.ffb.server.step.IStep;

@RulesCollection(RulesCollection.Rules.BB2025)
public class StandingUpCommand extends DeferredCommand {

	@Override
	public void execute(IStep step) {
		Game game = step.getGameState().getGame();
		game.getActingPlayer().setStandingUp(false);
	}

	@Override
	public DeferredCommandId getId() {
		return DeferredCommandId.STAND_UP;
	}

}
