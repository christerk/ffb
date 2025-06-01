package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ServerCommandSketchAddCoordinate extends ServerCommand {

	private String coach;
	private String sketchId;
	private FieldCoordinate coordinate;

	public ServerCommandSketchAddCoordinate() {
		super();
	}

	public ServerCommandSketchAddCoordinate(String coach, String sketchId, FieldCoordinate coordinate) {
		this.coach = coach;
		this.sketchId = sketchId;
		this.coordinate = coordinate;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_SKETCH_ADD_COORDINATE;
	}

	public String getCoach() {
		return coach;
	}

	public String getSketchId() {
		return sketchId;
	}

	public FieldCoordinate getCoordinate() {
		return coordinate;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.ID.addTo(jsonObject, sketchId);
		IJsonOption.COORDINATE.addTo(jsonObject, coordinate);
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ServerCommandSketchAddCoordinate initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		sketchId = IJsonOption.ID.getFrom(source, jsonObject);
		coordinate = IJsonOption.COORDINATE.getFrom(source, jsonObject);
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}

}
