package com.fumbbl.ffb.server.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.server.IServerJsonOption;

public class CarriedPlayer implements IJsonSerializable {

	private String playerId;
	private PlayerState oldState;
	private FieldCoordinate oldCoordinate;
	private boolean hasBall;

	public CarriedPlayer() {
	}

	public CarriedPlayer(String playerId, PlayerState oldState, FieldCoordinate oldCoordinate, boolean hasBall) {
		this.playerId = playerId;
		this.oldState = oldState;
		this.oldCoordinate = oldCoordinate;
		this.hasBall = hasBall;
	}

	public String getPlayerId() {
		return playerId;
	}

	public PlayerState getOldState() {
		return oldState;
	}

	public FieldCoordinate getOldCoordinate() {
		return oldCoordinate;
	}

	public boolean hasBall() {
		return hasBall;
	}

	@Override
	public CarriedPlayer initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IServerJsonOption.CARRIED_PLAYER_ID.getFrom(source, jsonObject);
		oldState = IServerJsonOption.OLD_CARRIED_PLAYER_STATE.getFrom(source, jsonObject);
		oldCoordinate = IServerJsonOption.OLD_CARRIED_PLAYER_COORDINATE.getFrom(source, jsonObject);
		hasBall = IServerJsonOption.CARRIED_PLAYER_HAS_BALL.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.CARRIED_PLAYER_ID.addTo(jsonObject, playerId);
		IServerJsonOption.OLD_CARRIED_PLAYER_STATE.addTo(jsonObject, oldState);
		IServerJsonOption.OLD_CARRIED_PLAYER_COORDINATE.addTo(jsonObject, oldCoordinate);
		IServerJsonOption.CARRIED_PLAYER_HAS_BALL.addTo(jsonObject, hasBall);
		return jsonObject;
	}
}

