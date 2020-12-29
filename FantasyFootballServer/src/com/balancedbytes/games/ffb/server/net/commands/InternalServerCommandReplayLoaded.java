package com.balancedbytes.games.ffb.server.net.commands;

import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.UtilNetCommand;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class InternalServerCommandReplayLoaded extends InternalServerCommand {

	private int fReplayToCommandNr;

	public InternalServerCommandReplayLoaded(long pGameId, int pReplayToCommandNr) {
		super(pGameId);
		fReplayToCommandNr = pReplayToCommandNr;
	}

	public NetCommandId getId() {
		return NetCommandId.INTERNAL_SERVER_REPLAY_LOADED;
	}

	public int getReplayToCommandNr() {
		return fReplayToCommandNr;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.REPLAY_TO_COMMAND_NR.addTo(jsonObject, fReplayToCommandNr);
		return jsonObject;
	}

	public InternalServerCommandReplayLoaded initFrom(Game game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		fReplayToCommandNr = IJsonOption.REPLAY_TO_COMMAND_NR.getFrom(game, jsonObject);
		return this;
	}

}
