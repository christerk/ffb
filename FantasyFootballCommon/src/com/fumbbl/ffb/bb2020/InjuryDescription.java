package com.fumbbl.ffb.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

public class InjuryDescription implements IJsonSerializable {
	private String playerId;
	private PlayerState playerState;
	private SeriousInjury seriousInjury;

	public InjuryDescription() {
	}

	public InjuryDescription(String playerId, PlayerState playerState, SeriousInjury seriousInjury) {
		this.playerId = playerId;
		this.playerState = playerState;
		this.seriousInjury = seriousInjury;
	}

	public String getPlayerId() {
		return playerId;
	}

	public PlayerState getPlayerState() {
		return playerState;
	}

	public SeriousInjury getSeriousInjury() {
		return seriousInjury;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.PLAYER_STATE.addTo(jsonObject, playerState);
		IJsonOption.SERIOUS_INJURY.addTo(jsonObject, seriousInjury);
		return jsonObject;
	}

	public InjuryDescription initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		playerState = IJsonOption.PLAYER_STATE.getFrom(source, jsonObject);
		seriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(source, jsonObject);
		return this;
	}
}
