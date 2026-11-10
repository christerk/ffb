package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.InjuryModificationOption;
import com.fumbbl.ffb.server.IServerJsonOption;

public class DecisionEdge implements IJsonSerializable {

	private InjuryModificationOption option;
	private int childId;

	public DecisionEdge() {
	}

	public DecisionEdge(InjuryModificationOption option, int childId) {
		this.option = option;
		this.childId = childId;
	}

	public InjuryModificationOption getOption() {
		return option;
	}

	public int getChildId() {
		return childId;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.INJURY_MODIFICATION_OPTION.addTo(jsonObject, option.toJsonValue());
		IServerJsonOption.CHILD_NODE_ID.addTo(jsonObject, childId);
		return jsonObject;
	}

	@Override
	public DecisionEdge initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		option = new InjuryModificationOption().initFrom(source,
			IServerJsonOption.INJURY_MODIFICATION_OPTION.getFrom(source, jsonObject));
		childId = IServerJsonOption.CHILD_NODE_ID.getFrom(source, jsonObject);
		return this;
	}
}
