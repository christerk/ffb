package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ReRollSource;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandUseReRoll extends ClientCommand {

	private ReRolledAction fReRolledAction;
	private ReRollSource fReRollSource;

	public ClientCommandUseReRoll() {
		super();
	}

	public ClientCommandUseReRoll(ReRolledAction pReRolledAction, ReRollSource pReRollSource) {
		fReRolledAction = pReRolledAction;
		fReRollSource = pReRollSource;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_USE_RE_ROLL;
	}

	public ReRolledAction getReRolledAction() {
		return fReRolledAction;
	}

	public ReRollSource getReRollSource() {
		return fReRollSource;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.RE_ROLLED_ACTION.addTo(jsonObject, fReRolledAction);
		IJsonOption.RE_ROLL_SOURCE.addTo(jsonObject, fReRollSource);
		return jsonObject;
	}

	public ClientCommandUseReRoll initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fReRolledAction = (ReRolledAction) IJsonOption.RE_ROLLED_ACTION.getFrom(game, jsonObject);
		fReRollSource = (ReRollSource) IJsonOption.RE_ROLL_SOURCE.getFrom(game, jsonObject);
		return this;
	}

}
