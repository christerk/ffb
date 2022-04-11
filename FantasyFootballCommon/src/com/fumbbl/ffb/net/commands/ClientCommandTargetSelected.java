package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandTargetSelected extends ClientCommand {

	private String targetPlayerId;

	public ClientCommandTargetSelected() {
	}

	public ClientCommandTargetSelected(String targetPlayerId) {
		this.targetPlayerId = targetPlayerId;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_TARGET_SELECTED;
	}

	public String getTargetPlayerId() {
		return targetPlayerId;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, targetPlayerId);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		super.initFrom(source, jsonObject);
		targetPlayerId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}
}
