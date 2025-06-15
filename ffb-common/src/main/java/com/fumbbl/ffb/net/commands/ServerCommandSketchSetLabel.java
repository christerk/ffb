package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.Arrays;
import java.util.List;

public class ServerCommandSketchSetLabel extends ServerCommand {

	private String coach;
	private List<String> sketchIds;
	private String label;

	public ServerCommandSketchSetLabel() {
		super();
	}

	public ServerCommandSketchSetLabel(String coach, List<String> sketchIds, String label) {
		this.coach = coach;
		this.sketchIds = sketchIds;
		this.label = label;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_SKETCH_SET_LABEL;
	}

	public String getCoach() {
		return coach;
	}

	public List<String> getSketchIds() {
		return sketchIds;
	}

	public String getLabel() {
		return label;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.IDS.addTo(jsonObject, sketchIds);
		IJsonOption.TEXT.addTo(jsonObject, label);
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ServerCommandSketchSetLabel initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		sketchIds = Arrays.asList(IJsonOption.IDS.getFrom(source, jsonObject));
		label = IJsonOption.TEXT.getFrom(source, jsonObject);
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}

}
