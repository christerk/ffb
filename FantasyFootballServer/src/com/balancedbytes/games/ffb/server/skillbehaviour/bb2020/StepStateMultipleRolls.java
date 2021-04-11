package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepStateMultipleRolls implements IJsonSerializable {
		public String goToLabelOnFailure;
		public List<String> reRollAvailableAgainst = new ArrayList<>();
		public List<String> blockTargets = new ArrayList<>();
		public boolean firstRun = true, teamReRollAvailable, proReRollAvailable;
		public ReRollSource reRollSource;
		public String reRollTarget;
		public Map<String, Integer> minimumRolls = new HashMap<>();
		public int initialCount;

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.GOTO_LABEL_ON_FAILURE.addTo(jsonObject, goToLabelOnFailure);
		IJsonOption.PLAYER_IDS.addTo(jsonObject, blockTargets);
		IJsonOption.PLAYER_ID.addTo(jsonObject, reRollTarget);
		IJsonOption.FIRST_RUN.addTo(jsonObject, firstRun);
		IJsonOption.PRO_RE_ROLL_OPTION.addTo(jsonObject, proReRollAvailable);
		IJsonOption.TEAM_RE_ROLL_OPTION.addTo(jsonObject, teamReRollAvailable);
		IJsonOption.RE_ROLL_AVAILABLE_AGAINST.addTo(jsonObject, reRollAvailableAgainst);
		IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, reRollSource);
		IJsonOption.MINIMUM_ROLLS.addTo(jsonObject, minimumRolls);
		IJsonOption.NUMBER.addTo(jsonObject, initialCount);
		return jsonObject;
	}

	@Override
	public StepStateMultipleRolls initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		goToLabelOnFailure = IServerJsonOption.GOTO_LABEL_ON_FAILURE.getFrom(game, jsonObject);
		blockTargets = Arrays.asList(IJsonOption.PLAYER_IDS.getFrom(game, jsonObject));
		reRollTarget = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		firstRun = IJsonOption.FIRST_RUN.getFrom(game, jsonObject);
		proReRollAvailable = IJsonOption.PRO_RE_ROLL_OPTION.getFrom(game, jsonObject);
		teamReRollAvailable = IJsonOption.TEAM_RE_ROLL_OPTION.getFrom(game, jsonObject);
		reRollAvailableAgainst = Arrays.asList(IJsonOption.RE_ROLL_AVAILABLE_AGAINST.getFrom(game, jsonObject));
		reRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(game, jsonObject);
		minimumRolls = IJsonOption.MINIMUM_ROLLS.getFrom(game, jsonObject);
		initialCount = IJsonOption.NUMBER.getFrom(game, jsonObject);
		return this;
	}
}
