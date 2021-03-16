package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.factory.IFactorySource;
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
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource game, JsonValue jsonValue) {
		return super.initFrom(game, jsonValue);
	}
}
