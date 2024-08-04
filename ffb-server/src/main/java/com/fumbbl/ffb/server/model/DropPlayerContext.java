package com.fumbbl.ffb.server.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.IServerJsonOption;
import com.fumbbl.ffb.server.InjuryResult;
import com.fumbbl.ffb.server.step.StepParameterKey;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DropPlayerContext implements IJsonSerializable {
	private InjuryResult injuryResult;
	private boolean endTurn, eligibleForSafePairOfHands, requiresArmourBreak, alreadyDropped,
		modifiedInjuryEndsTurn, endTurnWithoutKnockdown;
	private String label, playerId;
	private ApothecaryMode apothecaryMode;
	private StepParameterKey victimStateKey;
	private StepParameterKey[] additionalVictimStateKeys;

	public DropPlayerContext() {
	}

	public DropPlayerContext(InjuryResult injuryResult, String playerId, ApothecaryMode apothecaryMode, boolean alreadyDropped) {
		this(injuryResult, false, false, null, playerId, apothecaryMode, false, alreadyDropped);
	}

	public DropPlayerContext(InjuryResult injuryResult, boolean endTurn, boolean eligibleForSafePairOfHands, String label,
													 String playerId, ApothecaryMode apothecaryMode, boolean requiresArmourBreak) {
		this(injuryResult, endTurn, eligibleForSafePairOfHands, label, playerId, apothecaryMode, requiresArmourBreak, null);
	}

	public DropPlayerContext(InjuryResult injuryResult, boolean endTurn, boolean eligibleForSafePairOfHands, String label,
													 String playerId, ApothecaryMode apothecaryMode, boolean requiresArmourBreak, boolean alreadyDropped) {
		this(injuryResult, endTurn, eligibleForSafePairOfHands, label, playerId, apothecaryMode, requiresArmourBreak, alreadyDropped, null);
	}

	public DropPlayerContext(InjuryResult injuryResult, boolean endTurn, boolean eligibleForSafePairOfHands, String label,
													 String playerId, ApothecaryMode apothecaryMode, boolean requiresArmourBreak, StepParameterKey victimStateKey) {
		this(injuryResult, endTurn, eligibleForSafePairOfHands, label, playerId, apothecaryMode, requiresArmourBreak, false, victimStateKey);
	}

	public DropPlayerContext(InjuryResult injuryResult, boolean endTurn, boolean eligibleForSafePairOfHands, String label,
													 String playerId, ApothecaryMode apothecaryMode, boolean requiresArmourBreak, StepParameterKey victimStateKey, StepParameterKey[] additionalVictimStateKeys) {
		this(injuryResult, endTurn, eligibleForSafePairOfHands, label, playerId, apothecaryMode, requiresArmourBreak, false, victimStateKey, false, false, additionalVictimStateKeys);
	}
	public DropPlayerContext(InjuryResult injuryResult, boolean endTurn, boolean eligibleForSafePairOfHands, String label,
														String playerId, ApothecaryMode apothecaryMode, boolean requiresArmourBreak, boolean alreadyDropped,
														StepParameterKey victimStateKey) {
		this(injuryResult, endTurn, eligibleForSafePairOfHands, label, playerId, apothecaryMode, requiresArmourBreak, alreadyDropped,
			victimStateKey,  false, false, null);
	}

	public DropPlayerContext(InjuryResult injuryResult, boolean endTurn, boolean eligibleForSafePairOfHands, String label,
														String playerId, ApothecaryMode apothecaryMode, boolean requiresArmourBreak, boolean alreadyDropped,
														StepParameterKey victimStateKey, boolean modifiedInjuryEndsTurn, boolean endTurnWithoutKnockdown, StepParameterKey[] additionalVictimStateKeys) {
		this.injuryResult = injuryResult;
		this.endTurn = endTurn;
		this.eligibleForSafePairOfHands = eligibleForSafePairOfHands;
		this.label = label;
		this.playerId = playerId;
		this.apothecaryMode = apothecaryMode;
		this.requiresArmourBreak = requiresArmourBreak;
		this.alreadyDropped = alreadyDropped;
		this.victimStateKey = victimStateKey;
		this.endTurnWithoutKnockdown = endTurnWithoutKnockdown;
		this.modifiedInjuryEndsTurn = modifiedInjuryEndsTurn;
		this.additionalVictimStateKeys = additionalVictimStateKeys;
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

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isAlreadyDropped() {
		return alreadyDropped;
	}

	public StepParameterKey getVictimStateKey() {
		return victimStateKey;
	}

	public boolean isModifiedInjuryEndsTurn() {
		return modifiedInjuryEndsTurn;
	}

	public boolean isEndTurnWithoutKnockdown() {
		return endTurnWithoutKnockdown;
	}

	public void setEndTurn(boolean endTurn) {
		this.endTurn = endTurn;
	}

	public StepParameterKey[] getAdditionalVictimStateKeys() {
		return additionalVictimStateKeys;
	}

	@Override
	public DropPlayerContext initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IServerJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		injuryResult = new InjuryResult().initFrom(source, IServerJsonOption.INJURY_RESULT.getFrom(source, jsonObject));
		endTurn = IServerJsonOption.END_TURN.getFrom(source, jsonObject);
		eligibleForSafePairOfHands = IServerJsonOption.ELIGIBLE_FOR_SAFE_PAIR_OF_HANDS.getFrom(source, jsonObject);
		apothecaryMode = (ApothecaryMode) IServerJsonOption.APOTHECARY_MODE.getFrom(source, jsonObject);
		label = IServerJsonOption.LABEL.getFrom(source, jsonObject);
		requiresArmourBreak = IServerJsonOption.REQUIRES_ARMOUR_BREAK.getFrom(source, jsonObject);
		alreadyDropped = IServerJsonOption.ALREADY_DROPPED.getFrom(source, jsonObject);
		if (IServerJsonOption.STEP_PARAMETER_KEY.isDefinedIn(jsonObject)) {
			victimStateKey = StepParameterKey.valueOf(IServerJsonOption.STEP_PARAMETER_KEY.getFrom(source, jsonObject));
		}
		if (IServerJsonOption.MODIFIED_INJURY_ENDS_TURN.isDefinedIn(jsonObject)) {
			modifiedInjuryEndsTurn = IServerJsonOption.MODIFIED_INJURY_ENDS_TURN.getFrom(source, jsonObject);
		}

		if (IServerJsonOption.END_TURN_WITHOUT_KNOCKDOWN.isDefinedIn(jsonObject)) {
			endTurnWithoutKnockdown = IServerJsonOption.END_TURN_WITHOUT_KNOCKDOWN.getFrom(source, jsonObject);
		}

		if (IJsonOption.STEP_PARAMETER_KEYS.isDefinedIn(jsonObject)) {
			additionalVictimStateKeys = Arrays.stream(IJsonOption.STEP_PARAMETER_KEYS.getFrom(source, UtilJson.toJsonObject(jsonValue)))
				.map(StepParameterKey::valueOf).toArray(StepParameterKey[]::new);
		}

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
		if (victimStateKey != null) {
			IServerJsonOption.STEP_PARAMETER_KEY.addTo(jsonObject, victimStateKey.name());
		}
		IServerJsonOption.MODIFIED_INJURY_ENDS_TURN.addTo(jsonObject, modifiedInjuryEndsTurn);
		IServerJsonOption.END_TURN_WITHOUT_KNOCKDOWN.addTo(jsonObject, endTurnWithoutKnockdown);
		if (ArrayTool.isProvided(additionalVictimStateKeys)) {
			String[] keys = Arrays.stream(additionalVictimStateKeys).map(StepParameterKey::name).collect(Collectors.toList()).toArray(new String[]{});
			IJsonOption.STEP_PARAMETER_KEYS.addTo(jsonObject, keys);
		}
		return jsonObject;
	}
}
