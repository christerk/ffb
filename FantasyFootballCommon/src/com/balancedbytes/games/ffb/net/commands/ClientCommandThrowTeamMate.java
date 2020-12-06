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
public class ClientCommandThrowTeamMate extends ClientCommand implements ICommandWithActingPlayer {

	private FieldCoordinate fTargetCoordinate;
	private String fThrownPlayerId;
	private String fActingPlayerId;

	public ClientCommandThrowTeamMate() {
		super();
	}

	public ClientCommandThrowTeamMate(String pActingPlayerId, String pThrownPlayerId) {
		fActingPlayerId = pActingPlayerId;
		fThrownPlayerId = pThrownPlayerId;
		fTargetCoordinate = null;
	}

	public ClientCommandThrowTeamMate(String pActingPlayerId, FieldCoordinate pTargetCoordinate) {
		fActingPlayerId = pActingPlayerId;
		fTargetCoordinate = pTargetCoordinate;
		fThrownPlayerId = null;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_THROW_TEAM_MATE;
	}

	public String getActingPlayerId() {
		return fActingPlayerId;
	}

	public String getThrownPlayerId() {
		return fThrownPlayerId;
	}

	public FieldCoordinate getTargetCoordinate() {
		return fTargetCoordinate;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
		IJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
		IJsonOption.TARGET_COORDINATE.addTo(jsonObject, fTargetCoordinate);
		return jsonObject;
	}

	public ClientCommandThrowTeamMate initFrom(JsonValue jsonValue) {
		super.initFrom(jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(jsonObject);
		fThrownPlayerId = IJsonOption.THROWN_PLAYER_ID.getFrom(jsonObject);
		fTargetCoordinate = IJsonOption.TARGET_COORDINATE.getFrom(jsonObject);
		return this;
	}

}
