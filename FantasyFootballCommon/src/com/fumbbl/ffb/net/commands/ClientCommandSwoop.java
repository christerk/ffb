package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

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

	public ClientCommandSwoop initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(game, jsonObject);
		fTargetCoordinate = IJsonOption.TARGET_COORDINATE.getFrom(game, jsonObject);
		return this;
	}

}
