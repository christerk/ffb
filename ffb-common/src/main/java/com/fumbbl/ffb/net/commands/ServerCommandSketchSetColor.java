package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.Arrays;
import java.util.List;

public class ServerCommandSketchSetColor extends ServerCommand {

	private String coach;
	private List<String> sketchIds;
	private int rbg;

	public ServerCommandSketchSetColor() {
		super();
	}

	public ServerCommandSketchSetColor(String coach, List<String> sketchIds, int rbg) {
		this.coach = coach;
		this.sketchIds = sketchIds;
		this.rbg = rbg;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_SKETCH_SET_COLOR;
	}

	public List<String> getSketchIds() {
		return sketchIds;
	}

	public String getCoach() {
		return coach;
	}

	public int getRbg() {
		return rbg;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.IDS.addTo(jsonObject, sketchIds);
		IJsonOption.RGB.addTo(jsonObject, rbg);
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ServerCommandSketchSetColor initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		sketchIds = Arrays.asList(IJsonOption.IDS.getFrom(source, jsonObject));
		rbg = IJsonOption.RGB.getFrom(source, jsonObject);
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}

}
