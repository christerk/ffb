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
public class ClientCommandBlock extends ClientCommand implements ICommandWithActingPlayer {

	private String fActingPlayerId;
	private String fDefenderId;
	private boolean fUsingStab, usingChainsaw, usingVomit;

	public ClientCommandBlock() {
		super();
	}

	public ClientCommandBlock(String pActingPlayerId, String pDefenderId, boolean pUsingStab, boolean usingChainsaw, boolean usingVomit) {
		fActingPlayerId = pActingPlayerId;
		fDefenderId = pDefenderId;
		fUsingStab = pUsingStab;
		this.usingChainsaw = usingChainsaw;
		this.usingVomit = usingVomit;
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

	public boolean isUsingChainsaw() {
		return usingChainsaw;
	}

	public boolean isUsingVomit() {
		return usingVomit;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.ACTING_PLAYER_ID.addTo(jsonObject, fActingPlayerId);
		IJsonOption.DEFENDER_ID.addTo(jsonObject, fDefenderId);
		IJsonOption.USING_STAB.addTo(jsonObject, fUsingStab);
		IJsonOption.USING_CHAINSAW.addTo(jsonObject, usingChainsaw);
		IJsonOption.USING_VOMIT.addTo(jsonObject, usingVomit);
		return jsonObject;
	}

	public ClientCommandBlock initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fActingPlayerId = IJsonOption.ACTING_PLAYER_ID.getFrom(source, jsonObject);
		fDefenderId = IJsonOption.DEFENDER_ID.getFrom(source, jsonObject);
		fUsingStab = IJsonOption.USING_STAB.getFrom(source, jsonObject);
		usingChainsaw = IJsonOption.USING_CHAINSAW.getFrom(source, jsonObject);
		usingVomit = IJsonOption.USING_VOMIT.getFrom(source, jsonObject);
		return this;
	}

}
