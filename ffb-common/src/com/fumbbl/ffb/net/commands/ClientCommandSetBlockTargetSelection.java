package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.BlockKind;
import com.fumbbl.ffb.net.NetCommandId;

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
	public ClientCommand initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		playerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		kind = BlockKind.valueOf(IJsonOption.BLOCK_KIND.getFrom(source, jsonObject));
		return this;
	}

}
