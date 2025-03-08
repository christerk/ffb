package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.Map;

public class ServerCommandAutomaticPlayerMarkings extends ServerCommand {

	private Map<String, String> markings;
	private int index;

	public ServerCommandAutomaticPlayerMarkings() {
		super();
	}

	public ServerCommandAutomaticPlayerMarkings(int index, Map<String, String> markings) {
		this.markings = markings;
		this.index = index;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.SERVER_AUTOMATIC_PLAYER_MARKINGS;
	}

	public int getIndex() {
		return index;
	}

	public Map<String, String> getMarkings() {
		return markings;
	}

	@Override
	public Object initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		this.markings = IJsonOption.MARKINGS.getFrom(source, jsonObject);
		this.index = IJsonOption.SELECTED_INDEX.getFrom(source, jsonObject);
		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.MARKINGS.addTo(jsonObject, markings);
		IJsonOption.SELECTED_INDEX.addTo(jsonObject, index);
		return jsonObject;
	}
}
