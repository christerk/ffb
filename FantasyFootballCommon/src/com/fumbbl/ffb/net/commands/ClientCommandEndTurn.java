package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * @author Kalimar
 */
public class ClientCommandEndTurn extends ClientCommand {

	private TurnMode turnMode;

	public ClientCommandEndTurn() {
		this(null);
	}

	public ClientCommandEndTurn(TurnMode turnMode) {
		super();
		this.turnMode = turnMode;
	}

	public TurnMode getTurnMode() {
		return turnMode;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_END_TURN;
	}

	// JSON serialization

	public ClientCommandEndTurn initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		turnMode = (TurnMode) IJsonOption.TURN_MODE.getFrom(game, UtilJson.toJsonObject(jsonValue));
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.TURN_MODE.addTo(jsonObject, turnMode);
		return jsonObject;
	}
}
