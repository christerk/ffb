package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class BlitzState implements IJsonSerializable {
	private String selectedPlayerId;

	public BlitzState() {
	}

	public BlitzState(String selectedPlayerId) {
		this.selectedPlayerId = selectedPlayerId;
	}

	public String getSelectedPlayerId() {
		return selectedPlayerId;
	}

	@Override
	public BlitzState initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		selectedPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.PLAYER_ID.addTo(jsonObject, selectedPlayerId);
		return jsonObject;
	}
}
