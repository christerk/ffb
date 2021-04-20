package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

/**
 * 
 * @author Kalimar
 */
public class ClientCommandGaze extends ClientCommand implements ICommandWithActingPlayer {

	private String fActingPlayerId;
	private String fVictimId;

	public ClientCommandGaze() {
		super();
	}

	public ClientCommandGaze(String pActingPlayerId, String pCatcherId) {
		fActingPlayerId = pActingPlayerId;
		fVictimId = pCatcherId;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_GAZE;
	}

	public String getActingPlayerId() {
		return fActingPlayerId;
	}

	public String getVictimId() {
		return fVictimId;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
		IJsonOption.VICTIM_ID.addTo(jsonObject, fVictimId);
		return jsonObject;
	}

	public ClientCommandGaze initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(game, jsonObject);
		fVictimId = IJsonOption.VICTIM_ID.getFrom(game, jsonObject);
		return this;
	}

}
