package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandUseChainsaw extends ClientCommand {
	private boolean usingChainsaw;

	public ClientCommandUseChainsaw() {
	}

	public ClientCommandUseChainsaw(boolean usingChainsaw) {
		this.usingChainsaw = usingChainsaw;
	}

	public boolean isUsingChainsaw() {
		return usingChainsaw;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_CHAINSAW;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.USING_CHAINSAW.addTo(jsonObject, usingChainsaw);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		usingChainsaw = IJsonOption.USING_CHAINSAW.getFrom(game, UtilJson.toJsonObject(jsonValue));
		return this;
	}
}
