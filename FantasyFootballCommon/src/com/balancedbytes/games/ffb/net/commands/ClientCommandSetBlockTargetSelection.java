package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class ClientCommandSetBlockTargetSelection extends ClientCommand {

	private String playerId;
	private boolean useStab;

	public ClientCommandSetBlockTargetSelection() {
	}

	public ClientCommandSetBlockTargetSelection(String playerId, boolean useStab) {
		this.playerId = playerId;
		this.useStab = useStab;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_SET_BLOCK_TARGET_SELECTION;
	}

	public String getPlayerId() {
		return playerId;
	}

	public boolean isUseStab() {
		return useStab;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, playerId);
		IJsonOption.USING_STAB.addTo(jsonObject, useStab);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		useStab = IJsonOption.USING_STAB.getFrom(game, jsonObject);
		return this;
	}

}
