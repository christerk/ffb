package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.Arrays;
import java.util.List;

public class ClientCommandSketchSetColor extends ClientSketchCommand {

	private List<String> sketchIds;
	private int rbg;

	public ClientCommandSketchSetColor() {
		super();
	}

	public ClientCommandSketchSetColor(List<String> sketchIds, int rbg) {
		this.sketchIds = sketchIds;
		this.rbg = rbg;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_SKETCH_SET_COLOR;
	}

	public List<String> getSketchIds() {
		return sketchIds;
	}

	public int getRbg() {
		return rbg;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.IDS.addTo(jsonObject, sketchIds);
		IJsonOption.RGB.addTo(jsonObject, rbg);
		return jsonObject;
	}

	public ClientCommandSketchSetColor initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		sketchIds = Arrays.asList(IJsonOption.IDS.getFrom(source, jsonObject));
		rbg = IJsonOption.RGB.getFrom(source, jsonObject);
		return this;
	}

}
