package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.ReRollSource;
import com.balancedbytes.games.ffb.ReRolledAction;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

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
