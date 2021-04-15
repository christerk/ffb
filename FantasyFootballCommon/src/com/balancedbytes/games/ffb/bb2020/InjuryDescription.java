package com.balancedbytes.games.ffb.bb2020;

import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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

	public InjuryDescription initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		playerState = IJsonOption.PLAYER_STATE.getFrom(game, jsonObject);
		seriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(game, jsonObject);
		return this;
	}
}
