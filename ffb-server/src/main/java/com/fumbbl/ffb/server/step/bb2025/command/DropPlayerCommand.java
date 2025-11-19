package com.fumbbl.ffb.server.step.bb2025.command;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.step.DeferredCommand;
import com.fumbbl.ffb.server.step.DeferredCommandId;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.util.UtilServerInjury;

@RulesCollection(RulesCollection.Rules.BB2025)
public class DropPlayerCommand extends DeferredCommand {
	private String playerId;
	private ApothecaryMode apothecaryMode;
	private boolean eligibleForSafePairOfHands;

	@SuppressWarnings("unused")
	public DropPlayerCommand(){
		// for json deserialization
	}

	public DropPlayerCommand(String playerId, ApothecaryMode apothecaryMode, boolean eligibleForSafePairOfHands) {
		this.playerId = playerId;
		this.apothecaryMode = apothecaryMode;
		this.eligibleForSafePairOfHands = eligibleForSafePairOfHands;
	}

	@Override
	public void execute(IStep step) {
		Player<?> player = step.getGameState().getGame().getPlayerById(playerId);
		step.publishParameters(UtilServerInjury.dropPlayer(step, player, apothecaryMode, eligibleForSafePairOfHands));
	}

	@Override
	public DeferredCommandId getId() {
		return DeferredCommandId.DROP_PLAYER;
	}

	@Override
	public DropPlayerCommand initChildMember(IFactorySource source, JsonObject jsonObject) {
		apothecaryMode = (ApothecaryMode) IServerJsonOption.APOTHECARY_MODE.getFrom(source, jsonObject);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		eligibleForSafePairOfHands = IServerJsonOption.ELIGIBLE_FOR_SAFE_PAIR_OF_HANDS.getFrom(source, jsonObject);
		return null;
	}

	@Override
	public JsonObject addChildMember(JsonObject jsonObject) {
		IServerJsonOption.APOTHECARY_MODE.addTo(jsonObject, apothecaryMode);
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.ELIGIBLE_FOR_SAFE_PAIR_OF_HANDS.addTo(jsonObject, eligibleForSafePairOfHands);
		return jsonObject;
	}
}
