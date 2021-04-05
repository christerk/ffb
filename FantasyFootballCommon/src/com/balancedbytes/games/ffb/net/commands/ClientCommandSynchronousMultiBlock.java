package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Target;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class ClientCommandSynchronousMultiBlock extends ClientCommand {

	private List<Target> selectedTargets = new ArrayList<>();

	public ClientCommandSynchronousMultiBlock() {
	}

	public ClientCommandSynchronousMultiBlock(List<Target> selectedTargets) {
		this.selectedTargets = selectedTargets;
	}

	public List<Target> getSelectedTargets() {
		return selectedTargets;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_SYNCHRONOUS_MULTI_BLOCK;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		JsonArray jsonArray = new JsonArray();
		selectedTargets.stream().map(Target::toJsonValue).forEach(jsonArray::add);
		IJsonOption.SELECTED_BLOCK_TARGETS.addTo(jsonObject, jsonArray);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonArray jsonArray = IJsonOption.SELECTED_BLOCK_TARGETS.getFrom(game, jsonObject);
		jsonArray.values().stream()
			.map(value -> new Target().initFrom(game, value))
			.limit(2)
			.forEach(value -> selectedTargets.add(value));
		return this;
	}

}
