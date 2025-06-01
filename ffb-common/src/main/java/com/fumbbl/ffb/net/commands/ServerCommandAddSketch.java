package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.sketch.Sketch;
import com.fumbbl.ffb.net.NetCommandId;

public class ServerCommandAddSketch extends ServerCommand {

	private String coach;
	private Sketch sketch;

	public ServerCommandAddSketch() {
		super();
	}

	public ServerCommandAddSketch(String coach, Sketch sketch) {
		this.coach = coach;
		this.sketch = sketch;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_ADD_SKETCH;
	}

	public String getCoach() {
		return coach;
	}

	public Sketch getSketch() {
		return sketch;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.SKETCH.addTo(jsonObject, sketch.toJsonValue());
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ServerCommandAddSketch initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		sketch = new Sketch(0).initFrom(source, IJsonOption.SKETCH.getFrom(source, jsonObject));
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}

}
