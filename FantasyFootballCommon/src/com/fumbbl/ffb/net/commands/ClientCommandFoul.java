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
public class ClientCommandFoul extends ClientCommand implements ICommandWithActingPlayer {

	private String fActingPlayerId;
	private String fDefenderId;
	private boolean usingChainsaw;

	public ClientCommandFoul() {
		super();
	}

	public ClientCommandFoul(String pActingPlayerId, String pDefenderId, boolean usingChainsaw) {
		fActingPlayerId = pActingPlayerId;
		fDefenderId = pDefenderId;
		this.usingChainsaw = usingChainsaw;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_FOUL;
	}

	public String getActingPlayerId() {
		return fActingPlayerId;
	}

	public String getDefenderId() {
		return fDefenderId;
	}

	public boolean isUsingChainsaw() {
		return usingChainsaw;
	}
// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
		IJsonOption.USING_CHAINSAW.addTo(jsonObject, usingChainsaw);
		return jsonObject;
	}

	public ClientCommandFoul initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(game, jsonObject);
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(game, jsonObject);
		usingChainsaw = IJsonOption.USING_CHAINSAW.getFrom(game, jsonObject);
		return this;
	}

}
