package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandSketchAddCoordinate extends ClientSketchCommand {

	private String sketchId;
	private FieldCoordinate coordinate;

	public ClientCommandSketchAddCoordinate() {
		super();
	}

	public ClientCommandSketchAddCoordinate(String sketchId, FieldCoordinate coordinate) {
		this.sketchId = sketchId;
		this.coordinate = coordinate;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_SKETCH_ADD_COORDINATE;
	}

	public String getSketchId() {
		return sketchId;
	}

	public FieldCoordinate getCoordinate() {
		return coordinate;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ID.addTo(jsonObject, sketchId);
		IJsonOption.COORDINATE.addTo(jsonObject, coordinate);
		return jsonObject;
	}

	public ClientCommandSketchAddCoordinate initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		sketchId = IJsonOption.ID.getFrom(source, jsonObject);
		coordinate = IJsonOption.COORDINATE.getFrom(source, jsonObject);
		return this;
	}

}
