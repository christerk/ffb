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
public class ClientCommandReplay extends ClientCommand {

	private long fGameId;
	private int fReplayToCommandNr;
	private String coach;

	public ClientCommandReplay() {
		super();
	}

	public ClientCommandReplay(long pGameId, int pReplayToCommandNr, String coach) {
		fGameId = pGameId;
		fReplayToCommandNr = pReplayToCommandNr;
		this.coach = coach;
	}

	public NetCommandId getId() {
		return NetCommandId.CLIENT_REPLAY;
	}

	public long getGameId() {
		return fGameId;
	}

	public int getReplayToCommandNr() {
		return fReplayToCommandNr;
	}

	public String getCoach() {
		return coach;
	}

// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = super.toJsonValue();
		IJsonOption.GAME_ID.addTo(jsonObject, fGameId);
		IJsonOption.REPLAY_TO_COMMAND_NR.addTo(jsonObject, fReplayToCommandNr);
		IJsonOption.COACH.addTo(jsonObject, coach);
		return jsonObject;
	}

	public ClientCommandReplay initFrom(IFactorySource source, JsonValue jsonValue) {
		super.initFrom(source, jsonValue);
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fGameId = IJsonOption.GAME_ID.getFrom(source, jsonObject);
		fReplayToCommandNr = IJsonOption.REPLAY_TO_COMMAND_NR.getFrom(source, jsonObject);
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		return this;
	}

}
