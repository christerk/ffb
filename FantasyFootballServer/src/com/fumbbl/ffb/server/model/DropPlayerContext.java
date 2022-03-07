package com.fumbbl.ffb.server.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;

public class DropPlayerContext implements IJsonSerializable {
	private InjuryResult injuryResult;
	private boolean endTurn, eligibleForSafePairOfHands, requiresArmourBreak, alreadyDropped;
	private String label, playerId;
	private ApothecaryMode apothecaryMode;

	public DropPlayerContext() {
	}

	public DropPlayerContext(InjuryResult injuryResult, String playerId, ApothecaryMode apothecaryMode, boolean alreadyDropped) {
		this(injuryResult, false, false, null, playerId, apothecaryMode, false, true);
	}

	public DropPlayerContext(InjuryResult injuryResult, boolean endTurn, boolean eligibleForSafePairOfHands, String label,
													 String playerId, ApothecaryMode apothecaryMode, boolean requiresArmourBreak) {
		this(injuryResult, endTurn, eligibleForSafePairOfHands, label, playerId, apothecaryMode, requiresArmourBreak, false);
	}

	private DropPlayerContext(InjuryResult injuryResult, boolean endTurn, boolean eligibleForSafePairOfHands, String label,
														String playerId, ApothecaryMode apothecaryMode, boolean requiresArmourBreak, boolean alreadyDropped) {
		this.injuryResult = injuryResult;
		this.endTurn = endTurn;
		this.eligibleForSafePairOfHands = eligibleForSafePairOfHands;
		this.label = label;
		this.playerId = playerId;
		this.apothecaryMode = apothecaryMode;
		this.requiresArmourBreak = requiresArmourBreak;
		this.alreadyDropped = alreadyDropped;
	}

	public InjuryResult getInjuryResult() {
		return injuryResult;
	}

	public boolean isEndTurn() {
		return endTurn;
	}

	public boolean isEligibleForSafePairOfHands() {
		return eligibleForSafePairOfHands;
	}

	public String getLabel() {
		return label;
	}

	public String getPlayerId() {
		return playerId;
	}

	public ApothecaryMode getApothecaryMode() {
		return apothecaryMode;
	}

	public boolean isRequiresArmourBreak() {
		return requiresArmourBreak;
	}

	public boolean isAlreadyDropped() {
		return alreadyDropped;
	}

	@Override
	public DropPlayerContext initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		injuryResult = new InjuryResult().initFrom(game, IServerJsonOption.INJURY_RESULT.getFrom(game, jsonObject));
		endTurn = IServerJsonOption.END_TURN.getFrom(game, jsonObject);
		eligibleForSafePairOfHands = IServerJsonOption.ELIGIBLE_FOR_SAFE_PAIR_OF_HANDS.getFrom(game, jsonObject);
		apothecaryMode = (ApothecaryMode) IServerJsonOption.APOTHECARY_MODE.getFrom(game, jsonObject);
		label = IServerJsonOption.LABEL.getFrom(game, jsonObject);
		requiresArmourBreak = IServerJsonOption.REQUIRES_ARMOUR_BREAK.getFrom(game, jsonObject);
		alreadyDropped = IServerJsonOption.ALREADY_DROPPED.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.INJURY_RESULT.addTo(jsonObject, injuryResult.toJsonValue());
		IServerJsonOption.END_TURN.addTo(jsonObject, endTurn);
		IServerJsonOption.APOTHECARY_MODE.addTo(jsonObject, apothecaryMode);
		IServerJsonOption.LABEL.addTo(jsonObject, label);
		IServerJsonOption.ELIGIBLE_FOR_SAFE_PAIR_OF_HANDS.addTo(jsonObject, eligibleForSafePairOfHands);
		IServerJsonOption.REQUIRES_ARMOUR_BREAK.addTo(jsonObject, requiresArmourBreak);
		IServerJsonOption.ALREADY_DROPPED.addTo(jsonObject, alreadyDropped);
		return jsonObject;
	}
}
