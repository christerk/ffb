package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.bb2020.InjuryDescription;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientCommandUseApothecaries extends ClientCommand {

	private final List<InjuryDescription> injuryDescriptions = new ArrayList<>();

	public ClientCommandUseApothecaries() { }

	public ClientCommandUseApothecaries(List<InjuryDescription> injuryDescriptions) {
		this();
		this.injuryDescriptions.addAll(injuryDescriptions);
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_APOTHECARIES;
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

	public ClientCommandUseApothecaries initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonArray jsonArray = IJsonOption.INJURY_DESCRIPTIONS.getFrom(game, jsonObject);
		if (jsonArray != null) {
			injuryDescriptions.addAll(jsonArray.values().stream().map(value -> new InjuryDescription().initFrom(game, value)).collect(Collectors.toList()));
		}
		return this;
	}

}