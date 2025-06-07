package com.fumbbl.ffb.model.sketch;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SketchState implements IJsonSerializable {
	private List<Sketch> sketches = new ArrayList<>();
	private List<String> highlightIds = new ArrayList<>();
	private FieldCoordinate previewCoordinate;
	private String activeSketchId;

	public SketchState() {
	}

	public SketchState(List<Sketch> sketches) {
		this.sketches = sketches;
	}

	public SketchState(List<Sketch> sketches, List<String> highlightIds, FieldCoordinate previewCoordinate, String activeSketchId) {
		this.sketches = sketches;
		this.highlightIds = highlightIds;
		this.previewCoordinate = previewCoordinate;
		this.activeSketchId = activeSketchId;
	}

	public List<Sketch> getSketches() {
		return sketches;
	}

	public List<String> getHighlightIds() {
		return highlightIds;
	}

	public FieldCoordinate getPreviewCoordinate() {
		return previewCoordinate;
	}

	public String getActiveSketchId() {
		return activeSketchId;
	}

	@Override
	public SketchState initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonArray sketchArray = IJsonOption.SKETCHES.getFrom(source, jsonObject);
		for (JsonValue sketchValue : sketchArray) {
			Sketch sketch = new Sketch(0);
			sketch.initFrom(source, sketchValue);
			sketches.add(sketch);
		}

		highlightIds = Arrays.asList(IJsonOption.IDS.getFrom(source, jsonObject));

		previewCoordinate = IJsonOption.COORDINATE.getFrom(source, jsonObject);

		return this;
	}

	@Override
	public JsonValue toJsonValue() {
		JsonObject json = new JsonObject();
		JsonArray sketchArray = new JsonArray();
		for (Sketch sketch : sketches) {
			sketchArray.add(sketch.toJsonValue());
		}
		IJsonOption.SKETCHES.addTo(json, sketchArray);
		IJsonOption.IDS.addTo(json, highlightIds);
		IJsonOption.COORDINATE.addTo(json, previewCoordinate);
		return json;
	}
}
