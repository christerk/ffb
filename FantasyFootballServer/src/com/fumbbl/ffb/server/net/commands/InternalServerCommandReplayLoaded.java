package com.fumbbl.ffb.server.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.UtilNetCommand;

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

	public InternalServerCommandReplayLoaded initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		fReplayToCommandNr = IJsonOption.REPLAY_TO_COMMAND_NR.getFrom(source, jsonObject);
		return this;
	}

}
