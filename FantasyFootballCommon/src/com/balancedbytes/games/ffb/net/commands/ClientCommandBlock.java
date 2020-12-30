package com.balancedbytes.games.ffb.net.commands;

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
public class ClientCommandBlock extends ClientCommand implements ICommandWithActingPlayer {

	private String fActingPlayerId;
	private String fDefenderId;
	private boolean fUsingStab;

	public ClientCommandBlock() {
		super();
	}

	public ClientCommandBlock(String pActingPlayerId, String pDefenderId, boolean pUsingStab) {
		fActingPlayerId = pActingPlayerId;
		fDefenderId = pDefenderId;
		fUsingStab = pUsingStab;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_BLOCK;
	}

	public String getActingPlayerId() {
		return fActingPlayerId;
	}

	public String getDefenderId() {
		return fDefenderId;
	}

	public boolean isUsingStab() {
		return fUsingStab;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
		IJsonOption.USING_STAB.addTo(jsonObject, fUsingStab);
		return jsonObject;
	}

	public ClientCommandBlock initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(game, jsonObject);
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(game, jsonObject);
		fUsingStab = IJsonOption.USING_STAB.getFrom(game, jsonObject);
		return this;
	}

}
