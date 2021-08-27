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
public class ClientCommandThrowTeamMate extends ClientCommand implements ICommandWithActingPlayer {

	private FieldCoordinate fTargetCoordinate;
	private String fThrownPlayerId;
	private String fActingPlayerId;
	private boolean kicked;

	public ClientCommandThrowTeamMate() {
		super();
	}

	public ClientCommandThrowTeamMate(String pActingPlayerId, String pThrownPlayerId) {
		this(pActingPlayerId, pThrownPlayerId, false);
	}

	public ClientCommandThrowTeamMate(String pActingPlayerId, String pThrownPlayerId, boolean kicked) {
		fActingPlayerId = pActingPlayerId;
		fThrownPlayerId = pThrownPlayerId;
		fTargetCoordinate = null;
		this.kicked = kicked;
	}

	public ClientCommandThrowTeamMate(String pActingPlayerId, FieldCoordinate pTargetCoordinate) {
		this(pActingPlayerId, pTargetCoordinate, false);
	}

	public ClientCommandThrowTeamMate(String pActingPlayerId, FieldCoordinate pTargetCoordinate, boolean kicked) {
		fActingPlayerId = pActingPlayerId;
		fTargetCoordinate = pTargetCoordinate;
		fThrownPlayerId = null;
		this.kicked = kicked;
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

	public boolean isKicked() {
		return kicked;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
		IJsonOption.THROWN_PLAYER_ID.addTo(jsonObject, fThrownPlayerId);
		IJsonOption.TARGET_COORDINATE.addTo(jsonObject, fTargetCoordinate);
		IJsonOption.KICKED.addTo(jsonObject, kicked);
		return jsonObject;
	}

	public ClientCommandThrowTeamMate initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(game, jsonObject);
		fThrownPlayerId = IJsonOption.THROWN_PLAYER_ID.getFrom(game, jsonObject);
		fTargetCoordinate = IJsonOption.TARGET_COORDINATE.getFrom(game, jsonObject);
		kicked = IJsonOption.KICKED.getFrom(game, jsonObject);
		return this;
	}

}
