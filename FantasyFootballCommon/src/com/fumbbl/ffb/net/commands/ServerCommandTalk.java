package com.fumbbl.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandTalk extends ServerCommand {

	private String fCoach;
	private List<String> fTalks;

	public ServerCommandTalk() {
		fTalks = new ArrayList<>();
	}

	public ServerCommandTalk(String pCoach, String pTalk) {
		this();
		fCoach = pCoach;
		addTalk(pTalk);
	}

	public ServerCommandTalk(String pCoach, String[] pTalk) {
		this();
		fCoach = pCoach;
		addTalks(pTalk);
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_TALK;
	}

	public String getCoach() {
		return fCoach;
	}

	public void addTalk(String pTalk) {
		if (StringTool.isProvided(pTalk)) {
			fTalks.add(pTalk);
		}
	}

	public void addTalks(String[] pTalk) {
		if (ArrayTool.isProvided(pTalk)) {
			for (String talk : pTalk) {
				addTalk(talk);
			}
		}
	}

	public String[] getTalks() {
		return fTalks.toArray(new String[fTalks.size()]);
	}

	public boolean isReplayable() {
		return false;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COACH.addTo(jsonObject, fCoach);
		IJsonOption.TALKS.addTo(jsonObject, fTalks);
		return jsonObject;
	}

	public ServerCommandTalk initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		fCoach = IJsonOption.COACH.getFrom(game, jsonObject);
		addTalks(IJsonOption.TALKS.getFrom(game, jsonObject));
		return this;
	}

}
