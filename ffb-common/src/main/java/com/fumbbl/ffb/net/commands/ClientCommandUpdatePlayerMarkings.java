package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandUpdatePlayerMarkings extends ClientCommand {

	private boolean auto;

	public ClientCommandUpdatePlayerMarkings() {
	}

	public ClientCommandUpdatePlayerMarkings(boolean auto) {
		this.auto = auto;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_UPDATE_PLAYER_MARKINGS;
	}

	public boolean isAuto() {
		return auto;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.USE_AUTO_MARKINGS.addTo(jsonObject, auto);
		return jsonObject;
	}

	public ClientCommandUpdatePlayerMarkings initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		auto = IJsonOption.USE_AUTO_MARKINGS.getFrom(source, jsonObject);
		return this;
	}
}
