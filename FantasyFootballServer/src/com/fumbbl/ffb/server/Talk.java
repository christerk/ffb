package com.fumbbl.ffb.server;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;

public class Talk {

	private String talk;
	private String coach;
	private SessionMode sessionMode;
	private long gameId;

	public Talk(long gameId, String coach, SessionMode sessionMode, String talk) {
		this.gameId = gameId;
		this.coach = coach;
		this.sessionMode = sessionMode;
		this.talk = talk;
	}


	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.TALK.addTo(jsonObject, talk);
		IJsonOption.COACH.addTo(jsonObject, coach);
		IServerJsonOption.SESSION_MODE.addTo(jsonObject, sessionMode.name());
		IServerJsonOption.GAME_ID.addTo(jsonObject, gameId);
		return jsonObject;
	}

	public Talk initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		talk = IJsonOption.TALK.getFrom(source, jsonObject);
		coach = IJsonOption.COACH.getFrom(source, jsonObject);
		sessionMode = SessionMode.valueOf(IServerJsonOption.SESSION_MODE.getFrom(source, jsonObject))	;
		gameId = IServerJsonOption.GAME_ID.getFrom(source, jsonObject);
		return this;
	}

}
