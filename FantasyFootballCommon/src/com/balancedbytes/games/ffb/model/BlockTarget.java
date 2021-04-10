package com.balancedbytes.games.ffb.model;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.IJsonSerializable;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class BlockTarget implements IJsonSerializable {
	private String playerId;
	private BlockKind kind;

	public BlockTarget() {
	}

	public BlockTarget(String playerId, BlockKind kind) {
		this.playerId = playerId;
		this.kind = kind;
	}

	public String getPlayerId() {
		return playerId;
	}

	public BlockKind getKind() {
		return kind;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.BLOCK_KIND.addTo(jsonObject, kind.name());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	@Override
	public BlockTarget initFrom(IFactorySource game, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		kind = BlockKind.valueOf(IJsonOption.BLOCK_KIND.getFrom(game, jsonObject));
		return this;
	}

}
