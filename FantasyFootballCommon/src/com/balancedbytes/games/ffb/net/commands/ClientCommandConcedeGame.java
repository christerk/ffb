package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.ConcedeGameStatus;
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
public class ClientCommandConcedeGame extends ClientCommand {

	private ConcedeGameStatus fConcedeGameStatus;

	public ClientCommandConcedeGame() {
		super();
	}

	public ClientCommandConcedeGame(ConcedeGameStatus pConcedeGameStatus) {
		fConcedeGameStatus = pConcedeGameStatus;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_CONCEDE_GAME;
	}

	public ConcedeGameStatus getConcedeGameStatus() {
		return fConcedeGameStatus;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.CONCEDE_GAME_STATUS.addTo(jsonObject, fConcedeGameStatus);
		return jsonObject;
	}

	public ClientCommandConcedeGame initFrom(IFactorySource game, JsonValue jsonValue) {
		super.initFrom(game, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fConcedeGameStatus = (ConcedeGameStatus) IJsonOption.CONCEDE_GAME_STATUS.getFrom(game, jsonObject);
		return this;
	}

}
