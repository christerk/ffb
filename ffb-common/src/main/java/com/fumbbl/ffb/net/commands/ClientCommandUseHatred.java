package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandUseHatred extends ClientCommand {

	private String targetId;

	public ClientCommandUseHatred() {
	}

	public ClientCommandUseHatred(String targetId) {
		this.targetId = targetId;
	}

	public String getTargetId() {
		return targetId;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_HATRED;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, targetId);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		targetId = IJsonOption.PLAYER_ID.getFrom(source, jsonObject);
		return this;
	}
}
