package com.fumbbl.ffb.bb2020;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ApothecaryType;
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

	private ApothecaryType apothecaryType = ApothecaryType.TEAM;

	public InjuryDescription() {
	}

	public InjuryDescription(String playerId, PlayerState playerState, SeriousInjury seriousInjury, ApothecaryType apothecaryType) {
		this.playerId = playerId;
		this.playerState = playerState;
		this.seriousInjury = seriousInjury;
		this.apothecaryType = apothecaryType;
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

	public ApothecaryType getApothecaryType() {
		return apothecaryType;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.PLAYER_STATE.addTo(jsonObject, playerState);
		IJsonOption.SERIOUS_INJURY.addTo(jsonObject, seriousInjury);
		IJsonOption.APOTHECARY_TYPE.addTo(jsonObject, apothecaryType.name());
		return jsonObject;
	}

	public InjuryDescription initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		playerState = IJsonOption.PLAYER_STATE.getFrom(source, jsonObject);
		seriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(source, jsonObject);
		if (IJsonOption.APOTHECARY_TYPE.isDefinedIn(jsonObject)) {
			apothecaryType = ApothecaryType.valueOf(IJsonOption.APOTHECARY_TYPE.getFrom(source, jsonObject));
		}
		return this;
	}
}
