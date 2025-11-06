package com.fumbbl.ffb.server.step.bb2025.command;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.util.UtilServerInjury;

public class DropPlayerCommand implements DeferredCommand {
	private final String playerId;
	private final ApothecaryMode apothecaryMode;
	private final boolean eligibleForSafePairOfHands;

	public DropPlayerCommand(String playerId, ApothecaryMode apothecaryMode, boolean eligibleForSafePairOfHands) {
		this.playerId = playerId;
		this.apothecaryMode = apothecaryMode;
		this.eligibleForSafePairOfHands = eligibleForSafePairOfHands;
	}

	@Override
	public void execute(IStep step) {
		Player<?> player = step.getGameState().getGame().getPlayerById(playerId);
		UtilServerInjury.dropPlayer(step, player, apothecaryMode, eligibleForSafePairOfHands);
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
