package com.balancedbytes.games.ffb.server.step.bb2020;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.server.IServerJsonOption;
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
		selectedPlayerId = IServerJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.PLAYER_ID.addTo(jsonObject, selectedPlayerId);
		return jsonObject;
	}
}
