package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ServerCommandSketchSetColor extends ServerCommand {

	private String coach;
	private String sketchId;
	private int rbg;

	public ServerCommandSketchSetColor() {
		super();
	}

	public ServerCommandSketchSetColor(String coach, String sketchId, int rbg) {
		this.coach = coach;
		this.sketchId = sketchId;
		this.rbg = rbg;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_SKETCH_SET_COLOR;
	}

	public String getSketchId() {
		return sketchId;
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
		IJsonOption.ID.addTo(jsonObject, sketchId);
		IJsonOption.RGB.addTo(jsonObject, rbg);
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ServerCommandSketchSetColor initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		sketchId = IJsonOption.ID.getFrom(source, jsonObject);
		rbg = IJsonOption.RGB.getFrom(source, jsonObject);
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}

}
