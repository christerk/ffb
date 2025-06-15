package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.sketch.Sketch;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.ArrayList;
import java.util.List;

public class ServerCommandAddSketches extends ServerCommand {

	private String coach;
	private List<Sketch> sketches = new ArrayList<>();

	public ServerCommandAddSketches() {
		super();
	}

	public ServerCommandAddSketches(String coach, List<Sketch> sketches) {
		this.coach = coach;
		this.sketches = sketches;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_ADD_SKETCHES;
	}

	public String getCoach() {
		return coach;
	}

	public List<Sketch> getSketches() {
		return sketches;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		JsonArray sketchArray = new JsonArray();
		for (Sketch sketch : sketches) {
			sketchArray.add(sketch.toJsonValue());
		}
		IJsonOption.SKETCHES.addTo(jsonObject, sketchArray);
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ServerCommandAddSketches initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		JsonArray sketchArray = IJsonOption.SKETCHES.getFrom(source, jsonObject);
		for (JsonValue sketchValue : sketchArray) {
			Sketch sketch = new Sketch(0);
			sketch.initFrom(source, sketchValue);
			sketches.add(sketch);
		}
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}

}
