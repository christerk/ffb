package com.fumbbl.ffb.server;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
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
	private String carriedPlayerId;
	private PlayerState oldCarriedPlayerState;
	private FieldCoordinate oldCarriedPlayerCoordinate;

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

	public String getCarriedPlayerId() {
		return carriedPlayerId;
	}

	public PlayerState getOldCarriedPlayerState() {
		return oldCarriedPlayerState;
	}

	public FieldCoordinate getOldCarriedPlayerCoordinate() {
		return oldCarriedPlayerCoordinate;
	}

	public void clearCarriedPlayer() {
		carriedPlayerId = null;
		oldCarriedPlayerState = null;
		oldCarriedPlayerCoordinate = null;
	}

	public void setCarriedPlayer(String carriedPlayerId, PlayerState oldCarriedPlayerState,
		FieldCoordinate oldCarriedPlayerCoordinate) {
		this.carriedPlayerId = carriedPlayerId;
		this.oldCarriedPlayerState = oldCarriedPlayerState;
		this.oldCarriedPlayerCoordinate = oldCarriedPlayerCoordinate;
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

		if (IServerJsonOption.CARRIED_PLAYER_ID.isDefinedIn(jsonObject)) {
			carriedPlayerId = IServerJsonOption.CARRIED_PLAYER_ID.getFrom(source, jsonObject);
		}

		if (IServerJsonOption.OLD_CARRIED_PLAYER_STATE.isDefinedIn(jsonObject)) {
			oldCarriedPlayerState = IServerJsonOption.OLD_CARRIED_PLAYER_STATE.getFrom(source, jsonObject);
		}

		if (IServerJsonOption.OLD_CARRIED_PLAYER_COORDINATE.isDefinedIn(jsonObject)) {
			oldCarriedPlayerCoordinate = IServerJsonOption.OLD_CARRIED_PLAYER_COORDINATE.getFrom(source, jsonObject);
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
		IServerJsonOption.CARRIED_PLAYER_ID.addTo(jsonObject, carriedPlayerId);
		IServerJsonOption.OLD_CARRIED_PLAYER_STATE.addTo(jsonObject, oldCarriedPlayerState);
		IServerJsonOption.OLD_CARRIED_PLAYER_COORDINATE.addTo(jsonObject, oldCarriedPlayerCoordinate);
		return jsonObject;
	}
}
