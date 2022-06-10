package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public class ClientCommandEndTurn extends ClientCommand {

	private TurnMode turnMode;
	private final Map<String, FieldCoordinate> playerCoordinates = new HashMap<>();

	public ClientCommandEndTurn() {
		this(null, null);
	}

	public ClientCommandEndTurn(TurnMode turnMode, Map<String, FieldCoordinate> playerCoordinates) {
		super();
		this.turnMode = turnMode;
		if (playerCoordinates != null) {
			this.playerCoordinates.putAll(playerCoordinates);
		}
	}

	public TurnMode getTurnMode() {
		return turnMode;
	}

	public Map<String, FieldCoordinate> getPlayerCoordinates() {
		return playerCoordinates;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_END_TURN;
	}

	// JSON serialization

	public ClientCommandEndTurn initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		turnMode = (TurnMode) IJsonOption.TURN_MODE.getFrom(source, jsonObject);
		if (IJsonOption.PLAYERS_AT_COORDINATES.isDefinedIn(jsonObject)) {
			playerCoordinates.putAll(IJsonOption.PLAYERS_AT_COORDINATES.getFrom(source, jsonObject));
		}
		return this;
	}

	@Override
	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.TURN_MODE.addTo(jsonObject, turnMode);
		IJsonOption.PLAYERS_AT_COORDINATES.addTo(jsonObject, playerCoordinates);
		return jsonObject;
	}
}
