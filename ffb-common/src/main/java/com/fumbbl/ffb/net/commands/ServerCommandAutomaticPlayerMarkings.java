package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.List;
import java.util.Map;

public class ServerCommandAutomaticPlayerMarkings extends ServerCommand {

	private List<Map<String, String>> markings;

	public ServerCommandAutomaticPlayerMarkings(List<Map<String, String>> markings) {
		this.markings = markings;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.SERVER_AUTOMATIC_PLAYER_MARKINGS;
	}

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		this.markings = IJsonOption.MARKINGS.getFrom(source, UtilJson.toJsonObject(jsonValue));
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.MARKINGS.addTo(jsonObject, markings);
		return jsonObject;
	}
}
