package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.BlockTarget;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.ArrayList;
import java.util.List;

public class ClientCommandSynchronousMultiBlock extends ClientCommand {

	private List<BlockTarget> selectedBlockTargets = new ArrayList<>();

	public ClientCommandSynchronousMultiBlock() {
	}

	public ClientCommandSynchronousMultiBlock(List<BlockTarget> selectedBlockTargets) {
		this.selectedBlockTargets = selectedBlockTargets;
	}

	public List<BlockTarget> getSelectedTargets() {
		return selectedBlockTargets;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.CLIENT_SYNCHRONOUS_MULTI_BLOCK;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		JsonArray jsonArray = new JsonArray();
		selectedBlockTargets.stream().map(BlockTarget::toJsonValue).forEach(jsonArray::add);
		IJsonOption.SELECTED_BLOCK_TARGETS.addTo(jsonObject, jsonArray);
		return jsonObject;
	}

	@Override
	public ClientCommand initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonArray jsonArray = IJsonOption.SELECTED_BLOCK_TARGETS.getFrom(source, jsonObject);
		jsonArray.values().stream()
			.map(value -> new BlockTarget().initFrom(source, value))
			.limit(2)
			.forEach(value -> selectedBlockTargets.add(value));
		return this;
	}

}
