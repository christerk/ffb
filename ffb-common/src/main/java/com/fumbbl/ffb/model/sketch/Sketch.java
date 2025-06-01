package com.fumbbl.ffb.model.sketch;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

import java.util.LinkedList;
import java.util.UUID;

public class Sketch implements IJsonSerializable {
	private String id = UUID.randomUUID().toString();
	private int rgb;
	private String label;
	private final LinkedList<FieldCoordinate> path;

	public Sketch(int rgb) {
		this.rgb = rgb;
		path = new LinkedList<>();
	}

	public LinkedList<FieldCoordinate> getPath() {
		return path;
	}

	public String getId() {
		return id;
	}

	public int getRgb() {
		return rgb;
	}

	public void setRgb(int rgb) {
		this.rgb = rgb;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void addCoordinate(FieldCoordinate coordinate) {
		if (!coordinate.equals(path.peekLast())) {
			path.addLast(coordinate);
		}
	}

	@Override
	public Sketch initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		id = IJsonOption.ID.getFrom(source, jsonObject);
		rgb = IJsonOption.RGB.getFrom(source, jsonObject);
		label = IJsonOption.TEXT.getFrom(source, jsonObject);
		IJsonOption.FIELD_COORDINATES.getFrom(source, jsonObject)
			.forEach(coordinateJson ->
				path.add(new FieldCoordinate().initFrom(source, coordinateJson)));
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.ID.addTo(jsonObject, id);
		IJsonOption.RGB.addTo(jsonObject, rgb);
		IJsonOption.TEXT.addTo(jsonObject, label);
		JsonArray pathJson = new JsonArray();
		path.stream().map(FieldCoordinate::toJsonValue).forEach(pathJson::add);
		IJsonOption.FIELD_COORDINATES.addTo(jsonObject, pathJson);
		return jsonObject;
	}
}
