package com.fumbbl.ffb.server.step.bb2025.command;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.step.DeferredCommand;

public class StandingUpCommand implements DeferredCommand {
	private final Game game;

	public StandingUpCommand(Game game) {
		this.game = game;
	}

	@Override
		public void execute() {
				game.getActingPlayer().setStandingUp(false);
		}
}
