package com.fumbbl.ffb.server.step.bb2025.command;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.IStep;

public class StandingUpCommand implements DeferredCommand {
	private final Game game;

	public StandingUpCommand(Game game) {
		this.game = game;
	}

	@Override
		public void execute(IStep step) {
				game.getActingPlayer().setStandingUp(false);
		}

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		//TODO
		return null;
	}

	@Override
	public JsonValue toJsonValue() {
		//TODO
		return null;
	}

}
