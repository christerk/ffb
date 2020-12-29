package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandGameTime extends ServerCommand {

	private long fGameTime;
	private long fTurnTime;

	public ServerCommandGameTime() {
		super();
	}

	public ServerCommandGameTime(long gameTime, long turnTime) {
		fGameTime = gameTime;
		fTurnTime = turnTime;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_GAME_TIME;
	}

	public void setGameTime(long gameTime) {
		fGameTime = gameTime;
	}

	public long getGameTime() {
		return fGameTime;
	}

	public void setTurnTime(long turnTime) {
		fTurnTime = turnTime;
	}

	public long getTurnTime() {
		return fTurnTime;
	}

	public boolean isReplayable() {
		return false;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.GAME_TIME.addTo(jsonObject, fGameTime);
		IJsonOption.TURN_TIME.addTo(jsonObject, fTurnTime);
		return jsonObject;
	}

	public ServerCommandGameTime initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(game, jsonObject));
		fGameTime = IJsonOption.GAME_TIME.getFrom(game, jsonObject);
		fTurnTime = IJsonOption.TURN_TIME.getFrom(game, jsonObject);
		return this;
	}

}
