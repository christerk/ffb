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

	public ServerCommandAutomaticPlayerMarkings() {
		super();
	}

	public ServerCommandAutomaticPlayerMarkings(List<Map<String, String>> markings) {
		this.markings = markings;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.SERVER_AUTOMATIC_PLAYER_MARKINGS;
	}

	public List<Map<String, String>> getMarkings() {
		return markings;
	}

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		this.markings = IJsonOption.MARKINGS.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.MARKINGS.addTo(jsonObject, markings);
		return jsonObject;
	}
}
