package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.Optional;

public class ClientCommandBlitzTargetSelected extends ClientCommand {

	private String targetPlayerId;

	public ClientCommandBlitzTargetSelected() {
	}

	public ClientCommandBlitzTargetSelected(String targetPlayerId) {
		this.targetPlayerId = targetPlayerId;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_BLITZ_TARGET_SELECTED;
	}

	public Optional<String> getTargetPlayerId() {
		return Optional.ofNullable(targetPlayerId);
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, targetPlayerId);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource game, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		super.initFrom(game, jsonObject);
		targetPlayerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}
}
