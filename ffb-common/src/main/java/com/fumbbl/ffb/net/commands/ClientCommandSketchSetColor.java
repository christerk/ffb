package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandSketchSetColor extends ClientSketchCommand {

	private String sketchId;
	private int rbg;

	public ClientCommandSketchSetColor() {
		super();
	}

	public ClientCommandSketchSetColor(String sketchId, int rbg) {
		this.sketchId = sketchId;
		this.rbg = rbg;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_SKETCH_SET_COLOR;
	}

	public String getSketchId() {
		return sketchId;
	}

	public int getRbg() {
		return rbg;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ID.addTo(jsonObject, sketchId);
		IJsonOption.RGB.addTo(jsonObject, rbg);
		return jsonObject;
	}

	public ClientCommandSketchSetColor initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		sketchId = IJsonOption.ID.getFrom(source, jsonObject);
		rbg = IJsonOption.RGB.getFrom(source, jsonObject);
		return this;
	}

}
