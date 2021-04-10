package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.BlockKind;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class ClientCommandSetBlockTargetSelection extends ClientCommand {

	private String playerId;
	private BlockKind kind;

	public ClientCommandSetBlockTargetSelection() {
	}

	public ClientCommandSetBlockTargetSelection(String playerId, BlockKind kind) {
		this.playerId = playerId;
		this.kind = kind;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_SET_BLOCK_TARGET_SELECTION;
	}

	public String getPlayerId() {
		return playerId;
	}

	public BlockKind getKind() {
		return kind;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.BLOCK_KIND.addTo(jsonObject, kind.name());
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		kind = BlockKind.valueOf(IJsonOption.BLOCK_KIND.getFrom(game, jsonObject));
		return this;
	}

}
