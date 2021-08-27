package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.bb2020.InjuryDescription;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientCommandUseIgors extends ClientCommand {

	private final List<InjuryDescription> injuryDescriptions = new ArrayList<>();

	public ClientCommandUseIgors() { }

	public ClientCommandUseIgors(List<InjuryDescription> injuryDescriptions) {
		this();
		this.injuryDescriptions.addAll(injuryDescriptions);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_IGORS;
	}

	public List<InjuryDescription> getInjuryDescriptions() {
		return injuryDescriptions;
	}

// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		JsonArray jsonArray = new JsonArray();
		injuryDescriptions.stream().map(InjuryDescription::toJsonValue).forEach(jsonArray::add);
		IJsonOption.INJURY_DESCRIPTIONS.addTo(jsonObject, jsonArray);
		return jsonObject;
	}

	public ClientCommandUseIgors initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonArray jsonArray = IJsonOption.INJURY_DESCRIPTIONS.getFrom(game, jsonObject);
		if (jsonArray != null) {
			injuryDescriptions.addAll(jsonArray.values().stream().map(value -> new InjuryDescription().initFrom(game, value)).collect(Collectors.toList()));
		}
		return this;
	}

}
