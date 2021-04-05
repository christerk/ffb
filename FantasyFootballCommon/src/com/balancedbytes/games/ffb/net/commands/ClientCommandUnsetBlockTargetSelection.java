package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class ClientCommandUnsetBlockTargetSelection extends ClientCommand {

	private String playerId;

	public ClientCommandUnsetBlockTargetSelection() {
	}

	public ClientCommandUnsetBlockTargetSelection(String playerId) {
		this.playerId = playerId;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_UNSET_BLOCK_TARGET_SELECTION;
	}

	public String getPlayerId() {
		return playerId;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

}
