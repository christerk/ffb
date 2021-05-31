package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

public class ClientCommandUseReRollForTarget extends ClientCommandUseReRoll {
	private String targetId;

	public ClientCommandUseReRollForTarget() {
		super();
	}

	public ClientCommandUseReRollForTarget(ReRolledAction pReRolledAction, ReRollSource pReRollSource, String targetId) {
		super(pReRolledAction, pReRollSource, proIndex);
		this.targetId = targetId;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_RE_ROLL_FOR_TARGET;
	}

	public String getTargetId() {
		return targetId;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.PLAYER_ID.addTo(jsonObject, targetId);
		return jsonObject;
	}

	public ClientCommandUseReRollForTarget initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		targetId = IJsonOption.PLAYER_ID.getFrom(game, jsonObject);
		return this;
	}

}
