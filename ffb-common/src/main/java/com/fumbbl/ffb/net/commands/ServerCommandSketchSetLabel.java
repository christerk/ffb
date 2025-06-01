package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ServerCommandSketchSetLabel extends ClientCommand {

	private String coach;
	private String sketchId;
	private String label;

	public ServerCommandSketchSetLabel() {
		super();
	}

	public ServerCommandSketchSetLabel(String coach, String sketchId, String label) {
		this.coach = coach;
		this.sketchId = sketchId;
		this.label = label;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_SKETCH_SET_LABEL;
	}

	public String getCoach() {
		return coach;
	}

	public String getSketchId() {
		return sketchId;
	}

	public String getLabel() {
		return label;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ID.addTo(jsonObject, sketchId);
		IJsonOption.TEXT.addTo(jsonObject, label);
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ServerCommandSketchSetLabel initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		sketchId = IJsonOption.ID.getFrom(source, jsonObject);
		label = IJsonOption.TEXT.getFrom(source, jsonObject);
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}

}
