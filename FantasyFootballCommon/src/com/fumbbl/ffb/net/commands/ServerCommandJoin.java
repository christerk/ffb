package com.fumbbl.ffb.net.commands;

import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType.FactoryContext;
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
public class ServerCommandJoin extends ServerCommand {

	private String fCoach;
	private ClientMode fClientMode;
	private List<String> fPlayerNames;
	private int fSpectators;

	public ServerCommandJoin() {
		fPlayerNames = new ArrayList<>();
	}

	public ServerCommandJoin(String pCoach, ClientMode pClientMode, String[] pPlayerNames, int pSpectators) {
		this();
		fCoach = pCoach;
		fClientMode = pClientMode;
		addPlayerNames(pPlayerNames);
		fSpectators = pSpectators;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_JOIN;
	}

	public String getCoach() {
		return fCoach;
	}

	public ClientMode getClientMode() {
		return fClientMode;
	}

	public String[] getPlayerNames() {
		return fPlayerNames.toArray(new String[fPlayerNames.size()]);
	}

	private void addPlayerName(String pPlayerName) {
		if (StringTool.isProvided(pPlayerName)) {
			fPlayerNames.add(pPlayerName);
		}
	}

	private void addPlayerNames(String[] pPlayerNames) {
		if (ArrayTool.isProvided(pPlayerNames)) {
			for (String player : pPlayerNames) {
				addPlayerName(player);
			}
		}
	}

	public int getSpectators() {
		return fSpectators;
	}

	public boolean isReplayable() {
		return false;
	}

	@Override
	public FactoryContext getContext() {
		return FactoryContext.APPLICATION;
	}
	
		// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.COACH.addTo(jsonObject, fCoach);
		IJsonOption.CLIENT_MODE.addTo(jsonObject, fClientMode);
		IJsonOption.SPECTATORS.addTo(jsonObject, fSpectators);
		IJsonOption.PLAYER_NAMES.addTo(jsonObject, fPlayerNames);
		return jsonObject;
	}

	public ServerCommandJoin initFrom(IFactorySource game, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(game, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(game, jsonObject));
		fCoach = IJsonOption.COACH.getFrom(game, jsonObject);
		fClientMode = (ClientMode) IJsonOption.CLIENT_MODE.getFrom(game, jsonObject);
		fSpectators = IJsonOption.SPECTATORS.getFrom(game, jsonObject);
		addPlayerNames(IJsonOption.PLAYER_NAMES.getFrom(game, jsonObject));
		return this;
	}

}
