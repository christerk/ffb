package com.fumbbl.ffb.model;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

public class BlockTarget implements IJsonSerializable {
	private String playerId;
	private BlockKind kind;
	private PlayerState originalPlayerState;

	public BlockTarget() {
	}

	public BlockTarget(String playerId, BlockKind kind, PlayerState originalPlayerState) {
		this.playerId = playerId;
		this.kind = kind;
		this.originalPlayerState = originalPlayerState;
	}

	public String getPlayerId() {
		return playerId;
	}

	public BlockKind getKind() {
		return kind;
	}

	public PlayerState getOriginalPlayerState() {
		return originalPlayerState;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.BLOCK_KIND.addTo(jsonObject, kind.name());
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.PLAYER_STATE_OLD.addTo(jsonObject, originalPlayerState);
		return jsonObject;
	}

	@Override
	public BlockTarget initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		kind = BlockKind.valueOf(IJsonOption.BLOCK_KIND.getFrom(source, jsonObject));
		originalPlayerState = IJsonOption.PLAYER_STATE_OLD.getFrom(source, jsonObject);
		return this;
	}

}
