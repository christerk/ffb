package com.fumbbl.ffb.server;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ActiveEffects implements IJsonSerializable {

	private Weather oldWeather;
	private boolean skipRestoreWeather;
	private final Set<String> teamIdsAdditionalAssist = new HashSet<>();

	public Weather getOldWeather() {
		return oldWeather;
	}

	public void setOldWeather(Weather oldWeather) {
		this.oldWeather = oldWeather;
	}

	public boolean isSkipRestoreWeather() {
		return skipRestoreWeather;
	}

	public void setSkipRestoreWeather(boolean skipRestoreWeather) {
		this.skipRestoreWeather = skipRestoreWeather;
	}

	public void setTeamIdsAdditionalAssist(Set<String> teamIdsAdditionalAssist) {
		this.teamIdsAdditionalAssist.addAll(teamIdsAdditionalAssist);
	}

	public Set<String> getTeamIdsAdditionalAssist() {
		return teamIdsAdditionalAssist;
	}

	public void removeAdditionalAssist(String teamId) {
		teamIdsAdditionalAssist.remove(teamId);
	}

	@Override
	public ActiveEffects initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		oldWeather = (Weather) IServerJsonOption.WEATHER.getFrom(source, jsonObject);
		if (IServerJsonOption.SKIP_RESTORE_WEATHER.isDefinedIn(jsonObject)) {
			skipRestoreWeather = IServerJsonOption.SKIP_RESTORE_WEATHER.getFrom(source, jsonObject);
		}
		if (IServerJsonOption.TEAM_IDS_ADDITIONAL_ASSIST.isDefinedIn(jsonObject)) {
			teamIdsAdditionalAssist.addAll(Arrays.asList(IServerJsonOption.TEAM_IDS_ADDITIONAL_ASSIST.getFrom(source, jsonObject)));
		}

		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.WEATHER.addTo(jsonObject, oldWeather);
		IServerJsonOption.SKIP_RESTORE_WEATHER.addTo(jsonObject, skipRestoreWeather);
		IServerJsonOption.TEAM_IDS_ADDITIONAL_ASSIST.addTo(jsonObject, teamIdsAdditionalAssist);
		return jsonObject;
	}
}
