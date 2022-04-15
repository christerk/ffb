package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.IServerJsonOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepStateMultipleRolls implements IJsonSerializable, SingleReRollUseState {
	public String goToLabelOnFailure;
	public List<String> reRollAvailableAgainst = new ArrayList<>();
	public List<String> blockTargets = new ArrayList<>();
	public boolean firstRun = true, teamReRollAvailable, proReRollAvailable, consummateAvailable;
	public ReRollSource reRollSource;
	public ReRollSource singleUseReRollSource;
	public String reRollTarget;
	public Map<String, Integer> minimumRolls = new HashMap<>();
	public int initialCount;
	public String playerIdForSingleUseReRoll;

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToLabelOnFailure);
		IJsonOption.PLAYER_IDS.addTo(jsonObject, blockTargets);
		IJsonOption.PLAYER_ID.addTo(jsonObject, reRollTarget);
		IJsonOption.FIRST_RUN.addTo(jsonObject, firstRun);
		IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, proReRollAvailable);
		IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, teamReRollAvailable);
		IJsonOption.CONSUMMATE_OPTION.addTo(jsonObject, consummateAvailable);
		IJsonOption.RE_ROLL_AVAILABLE_AGAINST.addTo(jsonObject, reRollAvailableAgainst);
		IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, reRollSource);
		IJsonOption.MINIMUM_ROLLS.addTo(jsonObject, minimumRolls);
		IJsonOption.NUMBER.addTo(jsonObject, initialCount);
		IJsonOption.RE_ROLL_SOURCE_SINGLE_USE.addTo(jsonObject, singleUseReRollSource);
		IJsonOption.PLAYER_ID_SINGLE_USE_RE_ROLL.addTo(jsonObject, playerIdForSingleUseReRoll);
		return jsonObject;
	}

	@Override
	public StepStateMultipleRolls initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(source, jsonObject);
		blockTargets = Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(source, jsonObject));
		reRollTarget = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		firstRun = IJsonOption.FIRST_RUN.getFrom(source, jsonObject);
		proReRollAvailable = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(source, jsonObject);
		teamReRollAvailable = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(source, jsonObject);
		reRollAvailableAgainst = Arrays.asList(IJsonOption.RE_ROLL_AVAILABLE_AGAINST.getFrom(source, jsonObject));
		reRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(source, jsonObject);
		minimumRolls = IJsonOption.MINIMUM_ROLLS.getFrom(source, jsonObject);
		initialCount = IJsonOption.NUMBER.getFrom(source, jsonObject);
		singleUseReRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE_SINGLE_USE.getFrom(source, jsonObject);
		playerIdForSingleUseReRoll = IJsonOption.PLAYER_ID_SINGLE_USE_RE_ROLL.getFrom(source, jsonObject);
		consummateAvailable = IJsonOption.CONSUMMATE_OPTION.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public void setReRollSource(ReRollSource reRollSource) {
		this.reRollSource = reRollSource;
	}

	@Override
	public String getId() {
		return playerIdForSingleUseReRoll;
	}

	@Override
	public void setId(String playerId) {
		playerIdForSingleUseReRoll = playerId;
	}
}
