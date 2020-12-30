package com.balancedbytes.games.ffb.net.commands;

import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandSound extends ServerCommand {

	private SoundId fSound;

	public ServerCommandSound() {
		super();
	}

	public ServerCommandSound(SoundId pSound) {
		fSound = pSound;
	}

	public NetCommandId getId() {
		return NetCommandId.SERVER_SOUND;
	}

	public SoundId getSound() {
		return fSound;
	}

	public boolean isReplayable() {
		return false;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IJsonOption.NET_COMMAND_ID.addTo(jsonObject, getId());
		IJsonOption.COMMAND_NR.addTo(jsonObject, getCommandNr());
		IJsonOption.SOUND.addTo(jsonObject, fSound);
		return jsonObject;
	}

	public ServerCommandSound initFrom(IFactorySource source, JsonValue pJsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		fSound = (SoundId) IJsonOption.SOUND.getFrom(source, jsonObject);
		return this;
	}

}
