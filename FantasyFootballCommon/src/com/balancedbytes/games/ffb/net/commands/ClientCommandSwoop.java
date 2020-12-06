package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandSwoop extends ClientCommand implements ICommandWithActingPlayer {

	private FieldCoordinate fTargetCoordinate;
	private String fActingPlayerId;

	public ClientCommandSwoop() {
		super();
	}

	public ClientCommandSwoop(String pActingPlayerId, FieldCoordinate pTargetCoordinate) {
		fActingPlayerId = pActingPlayerId;
		fTargetCoordinate = pTargetCoordinate;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_SWOOP;
	}

	public String getActingPlayerId() {
		return fActingPlayerId;
	}

	public FieldCoordinate getTargetCoordinate() {
		return fTargetCoordinate;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
		IJsonOption.TARGET_COORDINATE.addTo(jsonObject, fTargetCoordinate);
		return jsonObject;
	}

	public ClientCommandSwoop initFrom(JsonValue jsonValue) {
		super.initFrom(jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(jsonObject);
		fTargetCoordinate = IJsonOption.TARGET_COORDINATE.getFrom(jsonObject);
		return this;
	}

}
