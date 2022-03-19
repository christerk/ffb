package com.fumbbl.ffb.net.commands;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.net.NetCommandId;

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

	public ServerCommandSound initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		UtilNetCommand.validateCommandId(this, (NetCommandId) IJsonOption.NET_COMMAND_ID.getFrom(source, jsonObject));
		setCommandNr(IJsonOption.COMMAND_NR.getFrom(source, jsonObject));
		fSound = (SoundId) IJsonOption.SOUND.getFrom(source, jsonObject);
		return this;
	}

}
