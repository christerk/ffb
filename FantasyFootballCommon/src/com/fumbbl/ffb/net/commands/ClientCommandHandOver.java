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
public class ClientCommandHandOver extends ClientCommand implements ICommandWithActingPlayer {

	private String fActingPlayerId;
	private String fCatcherId;

	public ClientCommandHandOver() {
		super();
	}

	public ClientCommandHandOver(String pActingPlayerId, String pCatcherId) {
		fActingPlayerId = pActingPlayerId;
		fCatcherId = pCatcherId;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_HAND_OVER;
	}

	public String getActingPlayerId() {
		return fActingPlayerId;
	}

	public String getCatcherId() {
		return fCatcherId;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
		IJsonOption.CATCHER_ID.addTo(jsonObject, fCatcherId);
		return jsonObject;
	}

	public ClientCommandHandOver initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(source, jsonObject);
		fCatcherId = IJsonOption.CATCHER_ID.getFrom(source, jsonObject);
		return this;
	}

}
