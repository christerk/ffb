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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InjuryDescription implements IJsonSerializable {
	private String playerId;
	private PlayerState playerState;
	private SeriousInjury seriousInjury;

	private List<ApothecaryType> apothecaryTypes = new ArrayList<>();

	public InjuryDescription() {
	}

	public InjuryDescription(String playerId, PlayerState playerState, SeriousInjury seriousInjury, List<ApothecaryType> apothecaryTypes) {
		this.playerId = playerId;
		this.playerState = playerState;
		this.seriousInjury = seriousInjury;
		this.apothecaryTypes = apothecaryTypes;
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

	public List<ApothecaryType> getApothecaryTypes() {
		return apothecaryTypes;
	}

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.PLAYER_STATE.addTo(jsonObject, playerState);
		IJsonOption.SERIOUS_INJURY.addTo(jsonObject, seriousInjury);
		IJsonOption.APOTHECARY_TYPES.addTo(jsonObject, apothecaryTypes.stream().map(ApothecaryType::name).collect(Collectors.toList()));
		return jsonObject;
	}

	public InjuryDescription initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		playerState = IJsonOption.PLAYER_STATE.getFrom(source, jsonObject);
		seriousInjury = (SeriousInjury) IJsonOption.SERIOUS_INJURY.getFrom(source, jsonObject);
		if (IJsonOption.APOTHECARY_TYPES.isDefinedIn(jsonObject)) {
			apothecaryTypes.addAll(Arrays.stream(IJsonOption.APOTHECARY_TYPES.getFrom(source, jsonObject)).map(ApothecaryType::valueOf).collect(Collectors.toList()));
		}
		return this;
	}
}
