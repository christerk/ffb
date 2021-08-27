package com.fumbbl.ffb.server.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.UtilNetCommand;
import com.fumbbl.ffb.server.IServerJsonOption;

/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandDeleteGame extends InternalServerCommand {

	private boolean fWithGamesInfo;

	public InternalServerCommandDeleteGame(long pGameId, boolean pWithGamesInfo) {
		super(pGameId);
		fWithGamesInfo = pWithGamesInfo;
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_DELETE_GAME;
	}

	public boolean isWithGamesInfo() {
		return fWithGamesInfo;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IServerJsonOption.WITH_GAMES_INFO.addTo(jsonObject, fWithGamesInfo);
		return jsonObject;
	}

	public InternalServerCommandDeleteGame initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		fWithGamesInfo = IServerJsonOption.WITH_GAMES_INFO.getFrom(game, jsonObject);
		return this;
	}

}
