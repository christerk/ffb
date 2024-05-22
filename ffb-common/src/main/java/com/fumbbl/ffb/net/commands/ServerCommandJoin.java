package com.fumbbl.ffb.net.commands;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kalimar
 */
public class ServerCommandJoin extends ServerCommand {

	private String fCoach;
	private ClientMode fClientMode;
	private final List<String> fPlayerNames, spectators;
	private int spectatorCount;

	public ServerCommandJoin() {
		fPlayerNames = new ArrayList<>();
		spectators = new ArrayList<>();
	}

	public ServerCommandJoin(String pCoach, ClientMode pClientMode, String[] pPlayerNames, List<String> spectators) {
		this();
		fCoach = pCoach;
		fClientMode = pClientMode;
		addPlayerNames(pPlayerNames);
		spectatorCount = spectators.size();
		this.spectators.addAll(spectators);

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
		return fPlayerNames.toArray(new String[0]);
	}
	public List<String> getSpectators() {
		return spectators;
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

	public int getSpectatorCount() {
		return spectatorCount;
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
		IJsonOption.SPECTATORS.addTo(jsonObject, spectatorCount);
		IJsonOption.PLAYER_NAMES.addTo(jsonObject, fPlayerNames);
		IJsonOption.SPECTATOR_NAMES.addTo(jsonObject, spectators);
		return jsonObject;
	}

	public ServerCommandJoin initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		fCoach = IJsonOption.COACH.getFrom(source, jsonObject);
		fClientMode = (ClientMode) IJsonOption.CLIENT_MODE.getFrom(source, jsonObject);
		addPlayerNames(IJsonOption.PLAYER_NAMES.getFrom(source, jsonObject));
		if (IJsonOption.SPECTATOR_NAMES.isDefinedIn(jsonObject)) {
			Collections.addAll(spectators, IJsonOption.SPECTATOR_NAMES.getFrom(source, jsonObject));
			spectatorCount = spectators.size();
		} else {
			spectatorCount = IJsonOption.SPECTATORS.getFrom(source, jsonObject);
		}
		return this;
	}

}
