package com.fumbbl.ffb.server;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActiveEffects implements IJsonSerializable {

	private Weather oldWeather;
	private boolean skipRestoreWeather, stalling;
	private final List<String> teamIdsAdditionalAssist = new ArrayList<>();
	private final List<String> shadowers = new ArrayList<>();
	private final Set<String> leaders = new HashSet<>();

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

	public List<String> getTeamIdsAdditionalAssist() {
		return teamIdsAdditionalAssist;
	}

	public void removeAdditionalAssist(String teamId) {
		teamIdsAdditionalAssist.removeIf(val -> val.equals(teamId));
	}

	public boolean isStalling() {
		return stalling;
	}

	public void setStalling(boolean stalling) {
		this.stalling = stalling;
	}

	public void clearShadowers() {
		shadowers.clear();
	}

	public void addShadower(String playerId) {
		shadowers.add(playerId);
	}

	public List<String> getShadowers() {
		return shadowers;
	}

	public void addLeader(String leader) {
		leaders.add(leader);
	}

	public Set<String> getLeaders() {
		return leaders;
	}

	public void clearLeaders() {
		leaders.clear();
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

		if (IServerJsonOption.STALLING.isDefinedIn(jsonObject)) {
			stalling = IServerJsonOption.STALLING.getFrom(source, jsonObject);
		}

		if (IServerJsonOption.PLAYER_IDS.isDefinedIn(jsonObject)) {
			shadowers.addAll(Arrays.asList(IServerJsonOption.PLAYER_IDS.getFrom(source, jsonObject)));
		}

		if (IServerJsonOption.LEADERS.isDefinedIn(jsonObject)) {
			leaders.addAll(Arrays.asList(IServerJsonOption.LEADERS.getFrom(source, jsonObject)));
		}

		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.WEATHER.addTo(jsonObject, oldWeather);
		IServerJsonOption.SKIP_RESTORE_WEATHER.addTo(jsonObject, skipRestoreWeather);
		IServerJsonOption.TEAM_IDS_ADDITIONAL_ASSIST.addTo(jsonObject, teamIdsAdditionalAssist);
		IServerJsonOption.STALLING.addTo(jsonObject, stalling);
		IServerJsonOption.PLAYER_IDS.addTo(jsonObject, shadowers);
		IServerJsonOption.LEADERS.addTo(jsonObject, leaders);
		return jsonObject;
	}
}
