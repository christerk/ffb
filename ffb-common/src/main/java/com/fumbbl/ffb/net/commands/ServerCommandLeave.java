package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.FactoryType.FactoryContext;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kalimar
 */
public class ServerCommandLeave extends ServerCommand {

	private String fCoach;
	private ClientMode fClientMode;
	private int spectatorCount;
	private List<String> spectators;

	public ServerCommandLeave() {
		super();
		spectators = new ArrayList<>();
	}

	public ServerCommandLeave(String pCoach, ClientMode pClientMode, List<String> pSpectators) {
		this();
		fCoach = pCoach;
		fClientMode = pClientMode;
		spectators = pSpectators;
		spectatorCount = pSpectators.size();
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_LEAVE;
	}

	public String getCoach() {
		return fCoach;
	}

	public ClientMode getClientMode() {
		return fClientMode;
	}

	public int getSpectatorCount() {
		return spectatorCount;
	}

	public List<String> getSpectators() {
		return spectators;
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
		IJsonOption.SPECTATOR_NAMES.addTo(jsonObject, spectators);
		return jsonObject;
	}

	public ServerCommandLeave initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		fCoach = IJsonOption.COACH.getFrom(source, jsonObject);
		fClientMode = (ClientMode) IJsonOption.CLIENT_MODE.getFrom(source, jsonObject);
		if (IJsonOption.SPECTATOR_NAMES.isDefinedIn(jsonObject)) {
			Collections.addAll(spectators, IJsonOption.SPECTATOR_NAMES.getFrom(source, jsonObject));
			spectatorCount = spectators.size();
		} else {
			spectatorCount = IJsonOption.SPECTATORS.getFrom(source, jsonObject);
		}
		return this;
	}

}
