package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class BlockTarget implements IJsonSerializable {
	private String playerId;
	private boolean useStab;

	public BlockTarget() {
	}

	public BlockTarget(String playerId, boolean useStab) {
		this.playerId = playerId;
		this.useStab = useStab;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isUseStab() {
		return useStab;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.USING_STAB.addTo(jsonObject, useStab);
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	@Override
	public BlockTarget initFrom(IFactorySource game, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		useStab = IJsonOption.USING_STAB.getFrom(game, jsonObject);
		return this;
	}
}
